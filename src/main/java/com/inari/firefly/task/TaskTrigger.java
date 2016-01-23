package com.inari.firefly.task;

import java.util.Arrays;
import java.util.Set;

import com.inari.firefly.Disposable;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.system.component.SystemComponent;

public abstract class TaskTrigger extends SystemComponent implements Disposable {

    public static final SystemComponentKey<TaskTrigger> TYPE_KEY = SystemComponentKey.create( TaskTrigger.class );
    
    public static final AttributeKey<Integer> TASK_ID = new AttributeKey<Integer>( "taskId", Integer.class, TaskTrigger.class );
    private static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        TASK_ID
    };
    
    protected int taskId;

    protected TaskTrigger( int id ) {
        super( id );
    }

    public final int getTaskId() {
        return taskId;
    }

    public final void setTaskId( int taskId ) {
        this.taskId = taskId;
    }
    
    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        Set<AttributeKey<?>> attributeKeys = super.attributeKeys();
        attributeKeys.addAll( Arrays.asList( ATTRIBUTE_KEYS ) );
        return attributeKeys;
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        super.fromAttributes( attributes );
        
        taskId = attributes.getValue( TASK_ID, taskId );
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        super.toAttributes( attributes );
        
        attributes.put( TASK_ID, taskId );
    }

}
