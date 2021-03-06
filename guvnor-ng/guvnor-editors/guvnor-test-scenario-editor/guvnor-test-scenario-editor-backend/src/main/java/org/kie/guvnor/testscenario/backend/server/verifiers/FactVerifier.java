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

package org.kie.guvnor.testscenario.backend.server.verifiers;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.kie.guvnor.testscenario.model.VerifyFact;
import org.kie.guvnor.testscenario.model.VerifyField;
import org.kie.runtime.KieSession;

public class FactVerifier {

    private final Map<String, Object> populatedData;
    private final TypeResolver        resolver;
    private final KieSession          ksession;
    private final Map<String, Object> globalData;
    private final ClassLoader         classLoader;

    public FactVerifier(Map<String, Object> populatedData,
                        TypeResolver resolver,
                        ClassLoader classLoader,
                        KieSession ksession,
                        Map<String, Object> globalData) {
        this.populatedData = populatedData;
        this.resolver = resolver;
        this.classLoader = classLoader;
        this.ksession = ksession;
        this.globalData = globalData;
    }

    public void verify(VerifyFact verifyFact) throws InvocationTargetException,
                                             NoSuchMethodException,
                                             InstantiationException,
                                             IllegalAccessException {

        //Clear existing results
        for ( VerifyField vf : verifyFact.getFieldValues() ) {
            vf.setSuccessResult( null );
            vf.setExplanation( "Fact of type [" + verifyFact.getName() + "] was not found in the results." );
        }

        if ( !verifyFact.anonymous ) {
            FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier( populatedData,
                                                                               verifyFact.getName(),
                                                                               getFactObject(
                                                                                              verifyFact.getName(),
                                                                                              populatedData,
                                                                                              globalData ),
                                                                               resolver,
                                                                               classLoader );
            fieldVerifier.checkFields( verifyFact.getFieldValues() );
        } else {
            for ( Object object : ksession.getObjects() ) {
                if ( verifyFact( object,
                                 verifyFact,
                                 populatedData,
                                 resolver ) ) {
                    return;
                }
            }
            for ( VerifyField verifyField : verifyFact.getFieldValues() ) {
                if ( verifyField.getSuccessResult() == null ) {
                    verifyField.setSuccessResult( Boolean.FALSE );
                    verifyField.setActualResult( "No match" );
                }
            }
        }
    }

    private boolean verifyFact(Object factObject,
                               VerifyFact verifyFact,
                               Map<String, Object> populatedData,
                               TypeResolver resolver) throws InvocationTargetException,
                                                     NoSuchMethodException,
                                                     InstantiationException,
                                                     IllegalAccessException {
        if ( factObject.getClass().getSimpleName().equals( verifyFact.getName() ) ) {
            FactFieldValueVerifier fieldVerifier = new FactFieldValueVerifier( populatedData,
                                                                               verifyFact.getName(),
                                                                               factObject,
                                                                               resolver,
                                                                               classLoader );
            fieldVerifier.checkFields( verifyFact.getFieldValues() );
            if ( verifyFact.wasSuccessful() ) {
                return true;
            }
        }
        return false;
    }

    private Object getFactObject(String factName,
                                 Map<String, Object> populatedData,
                                 Map<String, Object> globalData) {

        if ( populatedData.containsKey( factName ) ) {
            return populatedData.get( factName );
        } else {
            return globalData.get( factName );
        }
    }
}
