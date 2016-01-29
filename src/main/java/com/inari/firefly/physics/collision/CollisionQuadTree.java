package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.component.SystemComponent;

public final class CollisionQuadTree extends SystemComponent {
    
    public static final SystemComponentKey<CollisionQuadTree> TYPE_KEY = SystemComponentKey.create( CollisionQuadTree.class );
    
    public static final AttributeKey<Integer> VIEW_ID = new AttributeKey<Integer>( "viewId", Integer.class, CollisionQuadTree.class );
    public static final AttributeKey<Integer> LAYER_ID = new AttributeKey<Integer>( "layerId", Integer.class, CollisionQuadTree.class );
    public static final AttributeKey<Rectangle> WORLD_AREA = new AttributeKey<Rectangle>( "world_area", Rectangle.class, CollisionQuadTree.class );
    public static final AttributeKey<Integer> MAX_ENTRIES_OF_AREA = new AttributeKey<Integer>( "maxEntities", Integer.class, CollisionQuadTree.class );
    public static final AttributeKey<Integer> MAX_LEVEL = new AttributeKey<Integer>( "maxLevel", Integer.class, CollisionQuadTree.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        LAYER_ID,
        WORLD_AREA,
        MAX_ENTRIES_OF_AREA,
        MAX_LEVEL
    };

    private int viewId;
    private int layerId;
    private int maxEntities;
    private int maxLevel;
    private Node rootNode;
    
    private int matchingIndex;
    private final DynArray<IntIterator> matching;
    private final MatchingIterator matchingIterator = new MatchingIterator(); 
    private final Rectangle tmpBounds = new Rectangle();
    
    private final EntitySystem entitySystem;

    CollisionQuadTree( int id, FFContext context ) {
        super( id );
        this.entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        this.maxEntities = 10;
        this.maxLevel = 10;
        this.rootNode = null;
        
        matching = new DynArray<IntIterator>( maxLevel );
        matchingIndex = 0;
    }
    
    @Override
    public final IIndexedTypeKey indexedTypeKey() {
        return TYPE_KEY;
    };

    public final int getViewId() {
        return viewId;
    }

    public final void setViewId( int viewId ) {
        this.viewId = viewId;
    }

    public final int getLayerId() {
        return layerId;
    }

    public final void setLayerId( int layerId ) {
        this.layerId = layerId;
    }

    public final int getMaxEntities() {
        return maxEntities;
    }

    public final void setMaxEntities( int maxEntities ) {
        this.maxEntities = maxEntities;
    }

    public final int getMaxLevel() {
        return maxLevel;
    }

    public final void setMaxLevel( int maxLevel ) {
        this.maxLevel = maxLevel;
    }
    
    public final Rectangle getWorldArea() {
        if ( rootNode == null ) {
            return null;
        }
        
        return rootNode.area;
    }
    
    public final void setWorldArea( Rectangle worldArea ) {
        rootNode = new Node( 0, worldArea );
        matching.clear();
        matchingIndex = 0;
    }
    
    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }
    
    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        viewId = attributes.getValue( VIEW_ID, viewId );
        layerId = attributes.getValue( LAYER_ID, layerId );
        maxEntities = attributes.getValue( MAX_ENTRIES_OF_AREA, maxEntities );
        maxLevel = attributes.getValue( MAX_LEVEL, maxLevel );
        
        if ( attributes.contains( WORLD_AREA ) ) {
            setWorldArea( attributes.getValue( WORLD_AREA ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
        attributes.put( MAX_ENTRIES_OF_AREA, maxEntities );
        attributes.put( MAX_LEVEL, maxLevel );
        attributes.put( WORLD_AREA, rootNode.area );
    } 
    
    

    public final void add( int entityId ) {
        rootNode.add( 
            entityId, 
            getCollisionBounds( entityId )
        );
    }

    public final void remove( int entityId ) {
        rootNode.remove( entityId );
    }
    
    public final IntIterator get( int entityId ) {
        Rectangle collisionBounds = getCollisionBounds( entityId );
        matching.clear();
        matchingIndex = 0;
        rootNode.get( collisionBounds );
        if ( matching.contains( 0 ) ) {
            matchingIndex = 0;
            matchingIterator.currentIterator = matching.get( 0 );
        } else {
            matchingIndex = -1;
            matchingIterator.currentIterator = null;
        }
        return matchingIterator;
    }
    
    private final Rectangle getCollisionBounds( int entityId ) {
        ECollision collision = entitySystem.getComponent( entityId, ECollision.TYPE_KEY );
        ETransform tranform = entitySystem.getComponent( entityId, ETransform.TYPE_KEY );
        tmpBounds.x = (int) tranform.getXpos();
        tmpBounds.y = (int) tranform.getYpos();
        tmpBounds.width = collision.bounding.width;
        tmpBounds.height = collision.bounding.height;
        return tmpBounds;
    }

    
    public final class Node {
        
        final int level;
        final Rectangle area;
        final IntBag entities;
        final Node[] nodes;
        
        public Node( int level, Rectangle area ) {
            this.level = level;
            this.area = area;
            entities = new IntBag( maxEntities + 1, -1 );
            nodes = new Node[ 4 ];
        }
        
        final void remove( int entityId ) {
            entities.remove( entityId );
            
            for ( Node node : nodes ) {
                if ( node != null ) {
                    node.remove( entityId );
                }
            }
        }
        
        final void get( Rectangle bounds ) {
            Node node = getMatchingNode( bounds );
            if ( node != null && nodes[ 0 ] != null ) {
                node.get( bounds );
            }
          
            matching.set( matchingIndex, entities.iterator() );
            matchingIndex++;
        }
        
        final void add( int entityId, Rectangle bounds ) {
            if ( nodes[ 0 ] != null ) {
                Node node = getMatchingNode( bounds );
                
                if ( node != null ) {
                    node.add( entityId, bounds );
                    return;
                }
            }
            
            entities.add( entityId );
            
            if ( entities.size() > maxEntities && level < maxLevel ) {
                if ( nodes[ 0 ] == null ) { 
                    split(); 
                }
                
                for ( int i = 0; i < entities.length(); i++ ) {
                    if ( entities.isEmpty( i ) ) {
                        continue;
                    }

                    entityId = entities.removeAt( i );
                    bounds = getCollisionBounds( entityId );
                    Node node = getMatchingNode( bounds );
                    if ( node != null )  {
                        node.add( entityId, bounds );
                    } else {
                        entities.set( i, entityId );
                    }
                }
            }
        }
        
        private final Node getMatchingNode( Rectangle bounds ) {
            int vmp = area.x + area.width / 2;
            int hmp = area.y + area.height / 2;
            
            boolean top = ( bounds.y < hmp && bounds.y + bounds.height < hmp );
            boolean bottom = ( bounds.y > hmp );
            
            if ( bounds.x < vmp && bounds.x + bounds.width < vmp ) {
                if ( top ) {
                    return nodes[ 0 ];
                } else if ( bottom ) {
                    return nodes[ 3 ];
                }
            } else if ( bounds.x > vmp ) {
                if ( top ) {
                    return nodes[ 1 ];
                } else if ( bottom ) {
                    return nodes[ 2 ];
                }
            }
            
            return null;
        }
        
        private final void split() {
            int qWidth = area.width / 2;
            int qHeight = area.height / 2;
            int nextLevel = level + 1;
          
            nodes[ 0 ] = new Node( nextLevel, new Rectangle( area.x, area.y, qWidth, qHeight ) );
            nodes[ 1 ] = new Node( nextLevel, new Rectangle( area.x + qWidth, area.y, qWidth, qHeight ) );
            nodes[ 2 ] = new Node( nextLevel, new Rectangle( area.x + qWidth, area.y + qHeight, qWidth, qHeight ) );
            nodes[ 3 ] = new Node( nextLevel, new Rectangle( area.x, area.y + qHeight, qWidth, qHeight ) );
        }

        final void toString( StringBuilder builder ) {
            for ( int i = -1; i < level; i++ ) {
                builder.append( "  " );
            }
            builder.append( "Node [level=" );
            builder.append( level );
            builder.append( ", area=" );
            builder.append( area );
            builder.append( ", entities=" );
            builder.append( entities );
            builder.append( "]" );

            if ( nodes[ 0 ] != null ) {
                for ( int i = 0; i < nodes.length; i++ ) {
                    builder.append( "\n" );
                    nodes[ i ].toString( builder );
                }
            }
        }
    }
    
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EntityCollisionQuadTree: maxEntities=" ).append( maxEntities );
        builder.append( " maxLevel=" ).append( maxLevel ).append( "[\n" );
        rootNode.toString( builder );
        builder.append( "\n]" );
        return builder.toString();
    }
    
    private final class MatchingIterator implements IntIterator {
        
        IntIterator currentIterator = null;

        @Override
        public final boolean hasNext() {
            return currentIterator != null && ( currentIterator.hasNext() || matchingIndex >= 0 );
        }
        @Override
        public final int next() {
            int result = currentIterator.next();
            if ( !currentIterator.hasNext() ) {
                matchingIndex++;
                if ( matchingIndex < matching.capacity() && matching.contains( matchingIndex ) ) {
                    currentIterator = matching.get( matchingIndex );
                } else {
                    currentIterator = null;
                    matchingIndex = -1;
                }
            }
            return result;
        }
    }

}
