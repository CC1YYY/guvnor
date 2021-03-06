/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.guided.rule.client.editor;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.models.commons.shared.rule.DSLSentence;
import org.drools.guvnor.models.commons.shared.rule.IAction;
import org.drools.guvnor.models.commons.shared.rule.IPattern;
import org.drools.guvnor.models.commons.shared.rule.ActionCallMethod;
import org.drools.guvnor.models.commons.shared.rule.ActionGlobalCollectionAdd;
import org.drools.guvnor.models.commons.shared.rule.ActionInsertFact;
import org.drools.guvnor.models.commons.shared.rule.ActionRetractFact;
import org.drools.guvnor.models.commons.shared.rule.ActionSetField;
import org.drools.guvnor.models.commons.shared.rule.CompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.ExpressionFormLine;
import org.drools.guvnor.models.commons.shared.rule.FactPattern;
import org.drools.guvnor.models.commons.shared.rule.FreeFormLine;
import org.drools.guvnor.models.commons.shared.rule.FromAccumulateCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromCollectCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromEntryPointFactPattern;
import org.kie.guvnor.guided.rule.client.widget.ActionCallMethodWidget;
import org.kie.guvnor.guided.rule.client.widget.ActionInsertFactWidget;
import org.kie.guvnor.guided.rule.client.widget.ActionRetractFactWidget;
import org.kie.guvnor.guided.rule.client.widget.ActionSetFieldWidget;
import org.kie.guvnor.guided.rule.client.widget.CompositeFactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.DSLSentenceWidget;
import org.kie.guvnor.guided.rule.client.widget.ExpressionBuilder;
import org.kie.guvnor.guided.rule.client.widget.FactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.FreeFormLineWidget;
import org.kie.guvnor.guided.rule.client.widget.FromAccumulateCompositeFactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.FromCollectCompositeFactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.FromCompositeFactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.FromEntryPointFactPatternWidget;
import org.kie.guvnor.guided.rule.client.widget.GlobalCollectionAddWidget;
import org.kie.guvnor.guided.rule.client.widget.RuleModellerWidget;

public class RuleModellerWidgetFactory
        implements
        ModellerWidgetFactory {

    public RuleModellerWidget getWidget( RuleModeller ruleModeller,
                                         EventBus eventBus,
                                         IAction action,
                                         Boolean readOnly ) {
        if ( action instanceof ActionCallMethod) {
            return new ActionCallMethodWidget( ruleModeller,
                                               eventBus,
                                               (ActionCallMethod) action,
                                               readOnly );
        }
        if ( action instanceof ActionSetField) {
            return new ActionSetFieldWidget( ruleModeller,
                                             eventBus,
                                             (ActionSetField) action,
                                             readOnly );
        }
        if ( action instanceof ActionInsertFact) {
            return new ActionInsertFactWidget( ruleModeller,
                                               eventBus,
                                               (ActionInsertFact) action,
                                               readOnly );
        }
        if ( action instanceof ActionRetractFact) {
            return new ActionRetractFactWidget( ruleModeller,
                                                eventBus,
                                                (ActionRetractFact) action,
                                                readOnly );
        }
        if ( action instanceof DSLSentence) {
            RuleModellerWidget w = new DSLSentenceWidget( ruleModeller,
                                                          eventBus,
                                                          (DSLSentence) action,
                                                          readOnly );
            w.addStyleName( "model-builderInner-Background" ); //NON-NLS
            return w;
        }
        if ( action instanceof FreeFormLine) {
            return new FreeFormLineWidget( ruleModeller,
                                           eventBus,
                                           (FreeFormLine) action,
                                           readOnly );
        }
        if ( action instanceof ActionGlobalCollectionAdd) {
            return new GlobalCollectionAddWidget( ruleModeller,
                                                  eventBus,
                                                  (ActionGlobalCollectionAdd) action,
                                                  readOnly );
        }
        throw new RuntimeException( "I don't know what type of action is: " + action ); //NON-NLS
    }

    public RuleModellerWidget getWidget( RuleModeller ruleModeller,
                                         EventBus eventBus,
                                         IPattern pattern,
                                         Boolean readOnly ) {
        if ( pattern instanceof FactPattern) {
            return new FactPatternWidget( ruleModeller,
                                          eventBus,
                                          pattern,
                                          true,
                                          readOnly );
        }
        if ( pattern instanceof CompositeFactPattern ) {
            return new CompositeFactPatternWidget( ruleModeller,
                                                   eventBus,
                                                   (CompositeFactPattern) pattern,
                                                   readOnly );
        }
        if ( pattern instanceof FromAccumulateCompositeFactPattern ) {
            return new FromAccumulateCompositeFactPatternWidget( ruleModeller,
                                                                 eventBus,
                                                                 (FromAccumulateCompositeFactPattern) pattern,
                                                                 readOnly );
        }
        if ( pattern instanceof FromCollectCompositeFactPattern ) {
            return new FromCollectCompositeFactPatternWidget( ruleModeller,
                                                              eventBus,
                                                              (FromCollectCompositeFactPattern) pattern,
                                                              readOnly );
        }
        if ( pattern instanceof FromEntryPointFactPattern ) {
            return new FromEntryPointFactPatternWidget( ruleModeller,
                                                        eventBus,
                                                        (FromEntryPointFactPattern) pattern,
                                                        readOnly );
        }
        if ( pattern instanceof FromCompositeFactPattern) {
            return new FromCompositeFactPatternWidget( ruleModeller,
                                                       eventBus,
                                                       (FromCompositeFactPattern) pattern,
                                                       readOnly );
        }
        if ( pattern instanceof DSLSentence ) {
            return new DSLSentenceWidget( ruleModeller,
                                          eventBus,
                                          (DSLSentence) pattern,
                                          readOnly );
        }
        if ( pattern instanceof FreeFormLine ) {
            return new FreeFormLineWidget( ruleModeller,
                                           eventBus,
                                           (FreeFormLine) pattern,
                                           readOnly );
        }
        if ( pattern instanceof ExpressionFormLine) {
            return new ExpressionBuilder( ruleModeller,
                                          eventBus,
                                          (ExpressionFormLine) pattern,
                                          readOnly );
        }
        throw new RuntimeException( "I don't know what type of pattern is: " + pattern );

    }

    public boolean isTemplate() {
        return false;
    }
}
