/*
 * Copyright 2010 JBoss Inc
 *
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
 */

package org.kie.guvnor.testscenario.backend.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.drools.compiler.DroolsParserException;
import org.kie.KieServices;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.Message;
import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieModuleModel;
import org.kie.builder.model.KieSessionModel.KieSessionType;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieSession;
import org.kie.runtime.conf.ClockTypeOption;

/**
 * A class with some utilities for testing rules.
 */
public abstract class RuleUnit {

    /**
     * Return a wm ready to go based on the rules in a drl at the specified uri (in the classpath).
     */
    public KieSession getKieSession(String uri)
            throws DroolsParserException, IOException, Exception {
        
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write(org.kie.io.ResourceFactory.newClassPathResource( uri, getClass() ) )
                              .writeKModuleXML( createKieProjectWithPackages(ks).toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();
        
        List<Message> results = builder.getResults().getMessages();
        assertTrue( results.toString(), results.isEmpty() );
        
        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
   
        return ksession;
    }
    
    private KieModuleModel createKieProjectWithPackages(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage("*")
                .setDefault( true );

        kieBaseModel1.newKieSessionModel("KSession1")
                .setType( KieSessionType.STATEFUL )
                .setClockType(ClockTypeOption.get("pseudo"))
                .setDefault( true );

        return kproj;
    }    
}
