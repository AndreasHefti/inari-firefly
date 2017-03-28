package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.list.DynArray;
import com.inari.commons.lang.list.IntBag;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.graphics.ETransform;

public final class CollisionQuadTree extends ContactPool {
    
    public static final AttributeKey<Rectangle> WORLD_AREA = new AttributeKey<Rectangle>( "world_area", Rectangle.class, CollisionQuadTree.class );
    public static final AttributeKey<Integer> MAX_ENTRIES_OF_AREA = new AttributeKey<Integer>( "maxEntities", Integer.class, CollisionQuadTree.class );
    public static final AttributeKey<Integer> MAX_LEVEL = new AttributeKey<Integer>( "maxLevel", Integer.class, CollisionQuadTree.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        WORLD_AREA,
        MAX_ENTRIES_OF_AREA,
        MAX_LEVEL
    };

    private int maxEntities;
    private int maxLevel;
    private Node rootNode;
    
    private int matchingIndex;
    private final DynArray<IntIterator> matching;
    private final MatchingIterator matchingIterator = new MatchingIterator(); 
    private final Rectangle tmpBounds = new Rectangle();

    CollisionQuadTree( int id ) {
        super( id );
        
        this.maxEntities = 10;
        this.maxLevel = 10;
        this.rootNode = null;
        
        matching = DynArray.create( IntIterator.class, maxLevel, 10 );
        matchingIndex = 0;
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
        
        maxEntities = attributes.getValue( MAX_ENTRIES_OF_AREA, maxEntities );
        maxLevel = attributes.getValue( MAX_LEVEL, maxLevel );
        
        if ( attributes.contains( WORLD_AREA ) ) {
            setWorldArea( attributes.getValue( WORLD_AREA ) );
        }
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( MAX_ENTRIES_OF_AREA, maxEntities );
        attributes.put( MAX_LEVEL, maxLevel );
        attributes.put( WORLD_AREA, rootNode.area );
    } 

    @Override
    public final void add( int entityId ) {
        rootNode.add( 
            entityId, 
            getCollisionBounds( entityId )
        );
    }

    @Override
    public final void remove( int entityId ) {
        rootNode.remove( entityId );
    }
    
    @Override
    void update( int entityId ) {
        remove( entityId );
        add( entityId );
    }
    
    @Override
    public final IntIterator get( Rectangle scanBounds ) {
        matching.clear();
        matchingIndex = 0;
        
        if ( !GeomUtils.intersect( rootNode.area, scanBounds ) ) {
            return matchingIterator;
        }
        
        rootNode.get( scanBounds );
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
        Rectangle bounding = collision.getCollisionBounds();
        tmpBounds.width = bounding.width;
        tmpBounds.height = bounding.height;
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
            if ( node != null ) {
                node.get( bounds );
            } else {
                if ( nodes[ 0 ] == null ) {
                    matching.set( matchingIndex, entities.iterator() );
                    matchingIndex++;
                } else {
                    for ( int i = 0; i < nodes.length; i++ ) {
                        if ( GeomUtils.intersect( bounds, nodes[ i ].area ) ) {
                            matching.set( matchingIndex, nodes[ i ].entities.iterator() );
                            matchingIndex++;
                        }
                    }
                }
            } 
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

        @Override
        public final String toString() {
            StringBuilder builder = new StringBuilder();
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
                    builder.append( "\n" ).append( nodes[ i ] );
                }
            }
            
            return builder.toString();
        }
    }
    
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "EntityCollisionQuadTree: maxEntities=" ).append( maxEntities );
        builder.append( " maxLevel=" ).append( maxLevel ).append( "[\n" );
        builder.append( rootNode );
        builder.append( "\n]" );
        return builder.toString();
    }
    
    private final class MatchingIterator implements IntIterator {
        
        IntIterator currentIterator = null;

        @Override
        public final boolean hasNext() {
            return currentIterator != null && ( currentIterator.hasNext() || matchingIndex < 0 );
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
