/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell to display Conditions
 */
class ConditionCell extends AbstractCell<ConditionCol52> {

    interface FactPatternCellTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class=\"{0}\" >{1}</div>")
        SafeHtml text(String cssStyleName,
                      String message);
    }

    private static final FactPatternCellTemplate TEMPLATE = GWT.create( FactPatternCellTemplate.class );

    @Override
    public void render(Context context,
                       ConditionCol52 value,
                       SafeHtmlBuilder sb) {
        StringBuilder b = new StringBuilder();
        b.append( value.getFactField() );
        b.append( " : " );
        b.append( value.getFieldType() );
        sb.append( TEMPLATE.text( getCssStyleName( value ),
                                  b.toString() ) );
    }

    private String getCssStyleName(ConditionCol52 c) {
        //TODO Use Validator to check if condition is valid
        return "";
    }

}
