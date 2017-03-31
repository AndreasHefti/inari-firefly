package com.inari.firefly.physics.collision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.IntIterator;
import com.inari.commons.lang.aspect.Aspects;
import com.inari.commons.lang.indexed.IIndexedTypeKey;
import com.inari.firefly.FFInitException;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.ETransform;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.view.Layer;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.system.component.SystemComponent;

/** A ContactPool pools and organize the id's of all Entities for a given View and Layer that can collide
 *  (having a ECollision component).
 *  A ContactPool can be created for a specified View and Layer within the CollisionSystem and is managed within 
 *  the CollisionSystem. Every time an interesting Entity is activated or deactivated the pool gets automatically 
 *  updated by the CollisionSystem
 *  
 *  With get( area:Rectangle ) the CollisionSystem gets an Iteration of the best possible entity id's to test
 *  collision in the specified area. 
 *  
 *  There may be different implementations of ContactPool handling different cases of performance / efficiency needed.
 *  This can lend form a very simple implementation that just collects the interesting Entity id's and give them all back
 *  on request, to a implementation that minimizes the answer for the area request by also maximizing the performance to 
 *  do this.
 *
 */
public abstract class ContactPool extends SystemComponent {
    
    /** The Components type key. Use this to build, access, request and dispose a specified Component within the Context */
    public static final SystemComponentKey<ContactPool> TYPE_KEY = SystemComponentKey.create( ContactPool.class );
    
    /** The AtrributeKey to set/get the View id attribute value within the Views name */
    public static final AttributeKey<String> VIEW_NAME = AttributeKey.createString( "viewName", ContactPool.class );
    /** The AtrributeKey to set/get the View id attribute value */
    public static final AttributeKey<Integer> VIEW_ID = AttributeKey.createInt( "viewId", ContactPool.class );
    /** The AtrributeKey to set/get the Layer id attribute value within the Layers name */
    public static final AttributeKey<String> LAYER_NAME = AttributeKey.createString( "layerName", ContactPool.class );
    /** The AtrributeKey to set/get the Layer id attribute value */
    public static final AttributeKey<Integer> LAYER_ID = AttributeKey.createInt( "layerId", ContactPool.class );
    /** A list of all attributes natively supported by this Component or abstract Component */
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        VIEW_ID,
        LAYER_ID,
    };
    
    private final static Aspects MATCHER = EntityComponent.ASPECT_GROUP.createAspects( ETransform.TYPE_KEY, ECollision.TYPE_KEY );
    
    protected int viewId;
    protected int layerId;
    
    protected EntitySystem entitySystem;

    protected ContactPool( int index ) {
        super( index );
    }
    
    @Override
    public final void init() throws FFInitException {
        super.init();
        
        this.entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
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
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) ) );
        return attributeKeys;
    }
    
    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        viewId = attributes.getIdForName( VIEW_NAME, VIEW_ID, View.TYPE_KEY, viewId );
        layerId = attributes.getIdForName( LAYER_NAME, LAYER_ID, Layer.TYPE_KEY, layerId );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( VIEW_ID, viewId );
        attributes.put( LAYER_ID, layerId );
    } 

    /** Use this to directly register an entity id for a specified ContactPool instance.
     *  This first checks if the Entity is a valid entity by checking the availability of 
     *  an ETranform and an ECollision component and also the absence of an ETile component.
     *  
     *  (Tiles are not supported by ContactPools, they get checked against contacts within their TileGrid context)
     *  
     *  Also the equality of View and Layer id of the Entities Transform must match the View and Layer id of the ContactPool
     *  to get the entityId registered in a ContactPool. 
     *  All this check are done by registering and a User has not to concern about this
     *  
     * @param entityId the Entity id to add / register
     */
    public final void register( int entityId ) {
        final Aspects aspects = context.getEntityComponentAspects( entityId );
        if ( !aspects.include( MATCHER ) || aspects.contains( ETile.TYPE_KEY ) ) {
            return;
        }
        
        final ETransform transform = context.getEntityComponent( entityId, ETransform.TYPE_KEY );
        if ( this.viewId != transform.getViewId() || this.layerId != transform.getLayerId() ) {
            return;
        }
        
        add( entityId );
    }
    
    /** Implements the adding of an specific Entity id after all checks has passed. 
     *  This is called by the CollisionSystem on entity activation event and should not be called directly
     *  If you have to add a entity id directly to a pool, use register that also do the necessary checks before adding.
     * 
     * @param entityId the Entity id to add to the pool
     */
    protected abstract void add( int entityId  );
    
    /** Removes an specified Entity id from the pool.
     *  This Usually is called by the CollisionSystem on entity inactivation event but can also be called directly as an API
     * 
     * @param entityId Entity id to remove/unregister from the pool
     */
    public abstract void remove( int entityId );
    
    /** This is usually called by CollisionSystem in an entity move event and must update the entity in the pool 
     *  if the entity id has some orientation related store attributes within the specified ContactPool implementation.
     *  
     * @param entityId the Entity id of an entity that has just moved and changed its position in the world
     */
    public abstract void update( int entityId );
    
    /** Use this to get an IntIterator of all entity id's that most possibly has a collision within the given region.
     *  The efficiency of this depends on an specified implementation and can be different for different needs.
     *  
     * @param region The contact or collision region to check collision entity collisions against.
     * @return IntIterator of all entity id's that most possibly has a collision within the given region
     */
    public abstract IntIterator get( Rectangle region );
    
    /** Use this to clear all entity id's form a specified pool instance */
    public abstract void clear();

}
