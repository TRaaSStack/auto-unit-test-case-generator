/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * Copyright (C) 2021- SmartUt contributors
 *
 * SmartUt is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * SmartUt is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with SmartUt. If not, see <http://www.gnu.org/licenses/>.
 */

package org.smartut.instrumentation.error;

import org.smartut.classpath.ResourceList;
import org.smartut.setup.DependencyAnalysis;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * ErrorConditionClassAdapter class.
 * </p>
 * 
 * @author fraser
 */
public class ErrorConditionClassAdapter extends ClassVisitor {

	private final String className;

	private static final Logger logger = LoggerFactory.getLogger(ErrorConditionClassAdapter.class);

	/**
	 * <p>
	 * Constructor for ErrorConditionClassAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            a {@link org.objectweb.asm.ClassVisitor} object.
	 * @param className
	 *            a {@link java.lang.String} object.
	 */
	public ErrorConditionClassAdapter(ClassVisitor cv, String className) {
		super(Opcodes.ASM9, cv);
		this.className = className;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	/** {@inheritDoc} */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
	        String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("<clinit>"))
			return mv;

		if (!DependencyAnalysis.shouldInstrument(ResourceList.getClassNameFromResourcePath(className), name + desc))
			return mv;

		logger.info("Applying error transformation to " + className + ", method " + name
		        + desc);
		return new ErrorConditionMethodAdapter(mv, className, name, access, desc);
	}
}
