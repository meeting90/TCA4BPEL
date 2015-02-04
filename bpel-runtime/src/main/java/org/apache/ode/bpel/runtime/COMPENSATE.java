/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ode.bpel.runtime;

import org.apache.ode.bpel.o.OCompensate;
import org.apache.ode.bpel.o.OScope;
import org.apache.ode.jacob.ReceiveProcess;
import org.apache.ode.jacob.Synch;

import cn.edu.nju.cs.tcao4bpel.runtime.AspectFrame;


/**
 * Runtime implementation of the <code>&lt;compensate&gt;</code> activity.
 */
class COMPENSATE extends ACTIVITY {
    private static final long serialVersionUID = -467758076635337675L;
    private OCompensate _ocompact;

    public COMPENSATE(ActivityInfo self, ScopeFrame scopeFrame, LinkFrame linkFrame, AspectFrame aspectFrame) {
        super(self, scopeFrame, linkFrame, aspectFrame);
        _ocompact = (OCompensate) self.o;
    }

    public final void run() {
        OScope scopeToCompensate = _ocompact.compensatedScope;
        Synch sc = newChannel(Synch.class);
        _self.parent.compensate(scopeToCompensate,sc);
        object(new ReceiveProcess() {
            private static final long serialVersionUID = 3763991229748926216L;
        }.setChannel(sc).setReceiver(new Synch() {
            public void ret() {
                _self.parent.completed(null, CompensationHandler.emptySet());
            }
        }));
    }
}
