/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.rule;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.ObjectStoreWrapper;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectsInEntryPointCommand
    implements
    ExecutableCommand<Collection>, IdentifiableResult {

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    private ObjectFilter filter = null;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    public GetObjectsInEntryPointCommand() {
    }

    public GetObjectsInEntryPointCommand(ObjectFilter filter, String entryPoint) {
        this.filter = filter;
        this.entryPoint = entryPoint;
    }

    public GetObjectsInEntryPointCommand(ObjectFilter filter, String entryPoint, String outIdentifier) {
        this.filter = filter;
        this.entryPoint = entryPoint;
        this.outIdentifier = outIdentifier;
    }

    public Collection execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Collection col = null;

        if ( filter != null ) {

            col =  ep.getObjects( this.filter );
        } else {
            col =  ep.getObjects( );
        }

        if ( this.outIdentifier != null ) {
            List objects = new ArrayList( col );

            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult( this.outIdentifier, objects );
        }

        return col;
    }

    public Collection< ? extends Object > getObjects(StatefulKnowledgeSessionImpl session) {
        return new ObjectStoreWrapper( session.getObjectStore(),
                                       null,
                                       ObjectStoreWrapper.OBJECT );
    }

    public Collection< ? extends Object > getObjects(StatefulKnowledgeSessionImpl session, ObjectFilter filter) {
        return new ObjectStoreWrapper( session.getObjectStore(),
                                       filter,
                                       ObjectStoreWrapper.OBJECT );
    }

    public String toString() {
        if ( filter != null ) {
            return "session.getEntryPoint( " + entryPoint + " ).iterateObjects( " + filter + " );";
        } else {
            return "session.getEntryPoint( " + entryPoint + " ).iterateObjects();";
        }
    }

}
