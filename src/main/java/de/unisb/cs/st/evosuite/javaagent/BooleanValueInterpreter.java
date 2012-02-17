/**
 * 
 */
package de.unisb.cs.st.evosuite.javaagent;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

/**
 * An interpreter that determines which values are real Booleans
 * 
 * @author Gordon Fraser
 * 
 */
public class BooleanValueInterpreter extends BasicInterpreter {

	public final static BasicValue BOOLEAN_VALUE = new BasicValue(Type.BOOLEAN_TYPE);

	public final static BasicValue BOOLEAN_ARRAY = new BasicValue(Type.getType("[Z"));

	private final boolean isStatic;

	private final Type[] types;

	public BooleanValueInterpreter(String desc, boolean isStatic) {
		this.types = Type.getArgumentTypes(desc);
		this.isStatic = isStatic;
	}

	@Override
	public BasicValue newValue(final Type type) {
		if (type == null) {
			return BasicValue.UNINITIALIZED_VALUE;
		}
		switch (type.getSort()) {
		case Type.BOOLEAN:
			return BOOLEAN_VALUE;
		case Type.ARRAY:
			if (type.getElementType() == Type.BOOLEAN_TYPE)
				return BOOLEAN_ARRAY;
		default:
			return super.newValue(type);
		}
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.analysis.BasicInterpreter#unaryOperation(org.objectweb.asm.tree.AbstractInsnNode, org.objectweb.asm.tree.analysis.BasicValue)
	 */
	@Override
	public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value)
	        throws AnalyzerException {
		if (insn.getOpcode() == Opcodes.INSTANCEOF) {
			return BOOLEAN_VALUE;
		} else if (insn.getOpcode() == Opcodes.GETFIELD) {
			FieldInsnNode fieldNode = (FieldInsnNode) insn;
			if (BooleanTestabilityTransformation.isTransformedField(fieldNode.owner,
			                                                        fieldNode.name,
			                                                        fieldNode.desc))
				return BOOLEAN_VALUE;
		}
		return super.unaryOperation(insn, value);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.analysis.BasicInterpreter#newOperation(org.objectweb.asm.tree.AbstractInsnNode)
	 */
	@Override
	public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
		if (insn.getOpcode() == ICONST_0) {
			return BOOLEAN_VALUE;
		} else if (insn.getOpcode() == ICONST_1) {
			return BOOLEAN_VALUE;
		} else if (insn.getOpcode() == Opcodes.GETSTATIC) {
			FieldInsnNode fieldNode = (FieldInsnNode) insn;
			if (BooleanTestabilityTransformation.isTransformedField(fieldNode.owner,
			                                                        fieldNode.name,
			                                                        fieldNode.desc))
				return BOOLEAN_VALUE;

		}
		return super.newOperation(insn);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.analysis.BasicInterpreter#binaryOperation(org.objectweb.asm.tree.AbstractInsnNode, org.objectweb.asm.tree.analysis.BasicValue, org.objectweb.asm.tree.analysis.BasicValue)
	 */
	@Override
	public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1,
	        BasicValue value2) throws AnalyzerException {
		switch (insn.getOpcode()) {
		case IALOAD:
		case BALOAD:
		case CALOAD:
		case SALOAD:
			if (value1 == BOOLEAN_ARRAY)
				return BOOLEAN_VALUE;
		}
		return super.binaryOperation(insn, value1, value2);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.analysis.BasicInterpreter#copyOperation(org.objectweb.asm.tree.AbstractInsnNode, org.objectweb.asm.tree.analysis.BasicValue)
	 */
	@Override
	public BasicValue copyOperation(AbstractInsnNode insn, BasicValue value)
	        throws AnalyzerException {
		if (insn.getOpcode() == Opcodes.ILOAD) {
			VarInsnNode varNode = (VarInsnNode) insn;
			if (isStatic) {
				if (varNode.var < types.length) {
					if (types[varNode.var] == Type.BOOLEAN_TYPE)
						return BOOLEAN_VALUE;
				}
			} else {
				if (varNode.var > 0 && varNode.var - 1 < types.length) {
					if (types[varNode.var - 1] == Type.BOOLEAN_TYPE)
						return BOOLEAN_VALUE;
				}
			}
		}
		return super.copyOperation(insn, value);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.analysis.BasicInterpreter#merge(org.objectweb.asm.tree.analysis.BasicValue, org.objectweb.asm.tree.analysis.BasicValue)
	 */
	@Override
	public BasicValue merge(BasicValue v, BasicValue w) {
		if (v == BOOLEAN_VALUE && w == BasicValue.INT_VALUE)
			return BasicValue.INT_VALUE;
		else if (w == BOOLEAN_VALUE && v == BasicValue.INT_VALUE)
			return BasicValue.INT_VALUE;
		else
			return super.merge(v, w);
	}

}
