/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.analyzer.javacgopal.data;

import eu.fasten.core.data.FastenURI;

import org.opalj.ai.analyses.cg.UnresolvedMethodCall;
import org.opalj.br.MethodDescriptor;
import org.opalj.br.ReferenceType;

public class UnresolvedCall extends UnresolvedMethodCall {

    public UnresolvedCall(org.opalj.br.Method caller, int pc, ReferenceType calleeClass, String calleeName, MethodDescriptor calleeDescriptor) {
        super(caller, pc, calleeClass, calleeName, calleeDescriptor);
    }

    /**
     * Converts unresolved calls to URIs.
     * @param unresolvedCall Calls without specified product name in their target. Source or callers of
     *                        such calls are org.opalj.br.Method presented in the under investigation artifact,
     *                        But targets or callees don't have a product.
     * @return List of two dimensional eu.fasten.core.data.FastenURIs[] which always the first dimension of the array is a
     * fully resolved method and the second one has an unknown product.
     */
    public static FastenURI[] toURICall(UnresolvedMethodCall unresolvedCall) {

        FastenURI[] fastenURI = new FastenURI[2];

        var sourceURI = Method.toCanonicalSchemelessURI(
            null,
            unresolvedCall.caller().declaringClassFile().thisType(),
            unresolvedCall.caller().name(),
            unresolvedCall.caller().descriptor()
        );

        if (sourceURI != null) {

            var targetURI = Method.toCanonicalSchemelessURI(
                null,
                unresolvedCall.calleeClass(),
                unresolvedCall.calleeName(),
                unresolvedCall.calleeDescriptor()
            );

            if (targetURI != null) {
                fastenURI[0] = sourceURI;
                fastenURI[1] = FastenURI.create( "//" + targetURI.toString());
            }

        }
        return fastenURI;
    }

    /**
     * Converts unresolved calls to URIs.
     * @return List of two dimensional eu.fasten.core.data.FastenURIs[] which always the first dimension of the array is a
     * fully resolved method and the second one has an unknown product.
     */
    public FastenURI[] toURICall() {
        return toURICall(this);
    }

    @Override
    public boolean equals(Object obj) {
        var unresolvedCall = (UnresolvedCall) obj;
        if (this.calleeClass() == unresolvedCall.calleeClass()
            && this.calleeName() == unresolvedCall.calleeName()
            && this.calleeDescriptor() == unresolvedCall.calleeDescriptor()
            && this.caller() == unresolvedCall.caller()) {
            return true;

        }else {
        return false;
        }
    }

    @Override
    public int hashCode() {
        return this.calleeClass().hashCode() * this.calleeName().hashCode() * this.calleeDescriptor().hashCode() * this.caller().hashCode();
    }
}