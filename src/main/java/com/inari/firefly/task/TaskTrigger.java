package com.inari.firefly.task;

import com.inari.commons.config.StringConfigurable;
import com.inari.firefly.Disposable;
import com.inari.firefly.system.FFContext;

public interface TaskTrigger extends StringConfigurable, Disposable {

    void connect( FFContext context, Task task );

}
