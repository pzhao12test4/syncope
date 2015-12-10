/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.lib.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "userPatch")
@XmlType
public class UserPatch extends AnyPatch {

    private static final long serialVersionUID = 2872795537911821448L;

    private StringReplacePatchItem username;

    private PasswordPatch password;

    private LongReplacePatchItem securityQuestion;

    private StringReplacePatchItem securityAnswer;

    private BooleanReplacePatchItem mustChangePassword;

    private final Set<RelationshipPatch> relationships = new HashSet<>();

    private final Set<MembershipPatch> memberships = new HashSet<>();

    private final Set<StringPatchItem> roles = new HashSet<>();

    public StringReplacePatchItem getUsername() {
        return username;
    }

    public void setUsername(final StringReplacePatchItem username) {
        this.username = username;
    }

    public PasswordPatch getPassword() {
        return password;
    }

    public void setPassword(final PasswordPatch password) {
        this.password = password;
    }

    public LongReplacePatchItem getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(final LongReplacePatchItem securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public StringReplacePatchItem getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(final StringReplacePatchItem securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public BooleanReplacePatchItem getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(final BooleanReplacePatchItem mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    @XmlElementWrapper(name = "relationships")
    @XmlElement(name = "relationship")
    @JsonProperty("relationships")
    public Set<RelationshipPatch> getRelationships() {
        return relationships;
    }

    @XmlElementWrapper(name = "memberships")
    @XmlElement(name = "membership")
    @JsonProperty("memberships")
    public Set<MembershipPatch> getMemberships() {
        return memberships;
    }

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    @JsonProperty("roles")
    public Set<StringPatchItem> getRoles() {
        return roles;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty()
                && username == null && password == null && securityQuestion == null && securityAnswer == null
                && mustChangePassword == null && relationships.isEmpty() && memberships.isEmpty() && roles.isEmpty();
    }
}