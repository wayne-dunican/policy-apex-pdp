/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.context.parameters;

import org.onap.policy.apex.context.impl.locking.jvmlocal.JVMLocalLockManager;
import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;

/**
 * An empty lock manager parameter class that may be specialized by context lock manager plugins
 * that require plugin specific parameters. The class defines the default lock manager plugin as the
 * JVM local lock manager.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class LockManagerParameters extends AbstractParameters {
    /**
     * The default lock manager can lock context album instance across all threads in a single JVM.
     */
    public static final String DEFAULT_LOCK_MANAGER_PLUGIN_CLASS = JVMLocalLockManager.class.getCanonicalName();

    // Plugin class names
    private String pluginClass = DEFAULT_LOCK_MANAGER_PLUGIN_CLASS;

    /**
     * Constructor to create a lock manager parameters instance and register the instance with the
     * parameter service.
     */
    public LockManagerParameters() {
        super(LockManagerParameters.class.getCanonicalName());
        ParameterService.registerParameters(LockManagerParameters.class, this);
    }

    /**
     * Constructor to create a lock manager parameters instance with the name of a sub class of this
     * class and register the instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public LockManagerParameters(final String parameterClassName) {
        super(parameterClassName);
    }

    /**
     * Gets the plugin class.
     *
     * @return the plugin class
     */
    public String getPluginClass() {
        return pluginClass;
    }

    /**
     * Sets the plugin class.
     *
     * @param pluginClass the plugin class
     */
    public void setPluginClass(final String pluginClass) {
        this.pluginClass = pluginClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "LockManagerParameters [pluginClass=" + pluginClass + "]";
    }
}
