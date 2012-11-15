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
package org.jboss.as.console.client.mbui.cui.reification;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.mapping.MappingType;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceAttribute;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;
import org.jboss.as.console.client.mbui.cui.Context;
import org.jboss.as.console.client.mbui.cui.ReificationStrategy;
import org.jboss.as.console.client.mbui.cui.widgets.ModelNodeForm;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class FormStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        FormAdapter adapter = null;
        if (interactionUnit != null)
        {
            Map<String, ModelNode> descriptions = context.get (ContextKey.MODEL_DESCRIPTIONS);
            ModelNode modelDescription = descriptions.get(interactionUnit.getId().getNamespaceURI());
            assert modelDescription!=null : "Model description is required to execute FormStrategy";

            adapter = new FormAdapter(interactionUnit, modelDescription);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof org.jboss.as.console.client.mbui.aui.aim.as7.Form;
    }

    class FormAdapter implements ReificationWidget
    {
        final ModelNodeForm form;
        final InteractionUnit interactionUnit;
        private SafeHtmlBuilder helpTexts;

        FormAdapter(final InteractionUnit interactionUnit, final ModelNode modelDescription)
        {
            this.interactionUnit = interactionUnit;
            this.form = new ModelNodeForm();
            this.form.setNumColumns(2);
            this.form.setEnabled(false);

            assert modelDescription.hasDefined("attributes") : "Invalid model description";

            List<Property> attributeDescriptions = modelDescription.get("attributes").asPropertyList();

            ResourceMapping resourceMapping = (ResourceMapping)
                    this.interactionUnit.getMapping(MappingType.RESOURCE);

            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            List<FormItem> items = new ArrayList<FormItem>(attributes.size());

            helpTexts = new SafeHtmlBuilder();
            helpTexts.appendHtmlConstant("<table class='help-attribute-descriptions'>");

            for (ResourceAttribute attribute : attributes)
            {
                for(Property attr : attributeDescriptions)
                {
                    if(!attr.getName().equals(attribute.getName()))
                        continue;


                    char[] stringArray = attr.getName().toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);

                    String label = new String(stringArray).replace("-", " ");
                    ModelNode attrValue = attr.getValue();

                    // help
                    helpTexts.appendHtmlConstant("<tr class='help-field-row'>");
                    helpTexts.appendHtmlConstant("<td class='help-field-name'>");
                    helpTexts.appendEscaped(label).appendEscaped(": ");
                    helpTexts.appendHtmlConstant("</td>");
                    helpTexts.appendHtmlConstant("<td class='help-field-desc'>");
                    try {
                        helpTexts.appendHtmlConstant(attrValue.get("description").asString());
                    } catch (Throwable e) {
                        // ignore parse errors
                        helpTexts.appendHtmlConstant("<i>Failed to parse description</i>");
                    }
                    helpTexts.appendHtmlConstant("</td>");
                    helpTexts.appendHtmlConstant("</tr>");

                    ModelType type = ModelType.valueOf(attrValue.get("type").asString());
                    //System.out.println(attr.getName()+">"+type);
                    switch(type)
                    {
                        case BOOLEAN:
                            CheckBoxItem checkBoxItem = new CheckBoxItem(attr.getName(), label);
                            items.add(checkBoxItem);
                            break;
                        case DOUBLE:
                            NumberBoxItem num = new NumberBoxItem(attr.getName(), label);
                            items.add(num);
                            break;
                        case LONG:
                            NumberBoxItem num2 = new NumberBoxItem(attr.getName(), label);
                            items.add(num2);
                            break;
                        case INT:
                            NumberBoxItem num3 = new NumberBoxItem(attr.getName(), label);
                            items.add(num3);
                            break;
                        case STRING:
                            TextBoxItem tb = new TextBoxItem(attr.getName(), label);
                            items.add(tb);
                            break;
                        default:
                            throw new RuntimeException("Unsupported ModelType "+type);
                    }
                }
            }

            helpTexts.appendHtmlConstant("</table>");

            form.setFields(items.toArray(new FormItem[]{}));

        }

        @Override
        public void add(final ReificationWidget widget)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Widget asWidget()
        {

            VerticalPanel layout = new VerticalPanel();
            layout.setStyleName("fill-layout-width");
            layout.getElement().setAttribute("style", "margin-top:15px;");

            FormToolStrip<ModelNode> tools = new FormToolStrip<ModelNode>(
                    form,
                    new FormToolStrip.FormCallback<ModelNode>() {
                        @Override
                        public void onSave(Map<String, Object> changeset) {
                            // TODO: what's happening here ?
                        }

                        @Override
                        public void onDelete(ModelNode entity) {
                            // unsupported
                        }
                    });

            StaticHelpPanel help = new StaticHelpPanel(helpTexts.toSafeHtml());

            layout.add(tools.asWidget());
            layout.add(help.asWidget());
            layout.add(form.asWidget());

            return layout;
        }
    }
}