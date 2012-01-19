/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.dmr.client.ModelNode;

/**
 * Super interface for Views that use this framework.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface FrameworkView {
    
    /**
     * Retrieves this view as a {@link Widget} so that it can be inserted within
     * the DOM.
     * 
     * @return This view as a DOM object.
     */
    public Widget asWidget();
  
    /**
     * Call for Entities to be loaded into the view for the first time.
     */
    public void initialLoad();

    /**
     * Called whenever the data in the view needs to be refreshed.
     */
    public void refresh();
    
    /**
     * Tells the view if editing should be enabled.
     * 
     * @param isEnabled 
     */
    public void setEditingEnabled(boolean isEnabled);
}
