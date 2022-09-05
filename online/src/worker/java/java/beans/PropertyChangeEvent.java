/*
 * Copyright (c) 1996, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject {
	private static final long serialVersionUID = 7042693688939648123L;

	public PropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
		super(source);
		this.propertyName = propertyName;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setPropagationId(Object propagationId) {
		this.propagationId = propagationId;
	}

	public Object getPropagationId() {
		return propagationId;
	}

	private String propertyName;

	private Object newValue;

	private Object oldValue;

	private Object propagationId;

	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append("[propertyName=").append(getPropertyName());
		appendTo(sb);
		sb.append("; oldValue=").append(getOldValue());
		sb.append("; newValue=").append(getNewValue());
		sb.append("; propagationId=").append(getPropagationId());
		sb.append("; source=").append(getSource());
		return sb.append("]").toString();
	}

	void appendTo(StringBuilder sb) {
	}
}
