package org.smartut.assertion;

import org.smartut.testcase.execution.CodeUnderTestException;
import org.smartut.testcase.execution.ExecutionResult;
import org.smartut.testcase.execution.Scope;
import org.smartut.testcase.statements.AssignmentStatement;
import org.smartut.testcase.statements.MethodStatement;
import org.smartut.testcase.statements.PrimitiveStatement;
import org.smartut.testcase.statements.Statement;
import org.smartut.testcase.variable.ConstantValue;
import org.smartut.testcase.variable.VariableReference;
import org.smartut.utils.generic.GenericClass;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ContainsTraceObserver extends AssertionTraceObserver<ContainsTraceEntry> {

    /* (non-Javadoc)
	 * @see org.smartut.assertion.AssertionTraceObserver#visit(org.smartut.testcase.StatementInterface, org.smartut.testcase.Scope, org.smartut.testcase.VariableReference)
	 */
    /** {@inheritDoc} */
    @Override
    protected void visit(Statement statement, Scope scope, VariableReference var) {
        try {
            Object object = var.getObject(scope);
            if (object == null)
                return;

            if(statement instanceof AssignmentStatement)
                return;
            if(statement instanceof PrimitiveStatement<?>)
                return;

            // Only relevant for Collections
            if(!(object instanceof Collection))
                return;

            Collection<?> collectionObject = (Collection<?>)object;

            List<GenericClass> parameterClasses = var.getGenericClass().getParameterClasses();
            // Need to know exact type
            if(parameterClasses.size() != 1)
                return;

            java.lang.reflect.Type parameterType = parameterClasses.get(0).getType();


            ContainsTraceEntry entry = new ContainsTraceEntry(var);
            int position = statement.getPosition();

            Set<VariableReference> otherVariables = new LinkedHashSet<>(scope.getElements(parameterType));
            for(int i = 0; i <= statement.getPosition(); i++) {
                for(VariableReference candidateVar : currentTest.getStatement(i).getVariableReferences()) {
                    if(candidateVar instanceof ConstantValue && candidateVar.isAssignableTo(parameterType)) {
                        otherVariables.add(candidateVar);
                    }
                }
            }

            for (VariableReference other : otherVariables) {
                Object otherObject;
                if(other instanceof ConstantValue)
                    otherObject = ((ConstantValue)other).getValue();
                else
                    otherObject = other.getObject(scope);

                if (otherObject == null)
                    continue; // TODO: Don't do this?

                int otherPos = other.getStPosition();
                if(otherPos > position)
                    continue; // Don't compare with variables that are not defined - may happen with primitives?

                Statement otherStatement = currentTest.getStatement(otherPos);

                if(otherStatement instanceof MethodStatement) {
                    if(((MethodStatement)otherStatement).getMethodName().equals("hashCode"))
                        continue; // No comparison against hashCode, as the hashCode return value will not be in the test
                }

                try {
                    logger.debug("Checking whether {} contains {} is: {}", var, other,
                            collectionObject.contains(otherObject));
                    entry.addEntry(other, collectionObject.contains(otherObject));
                } catch (Throwable t) {
                    logger.debug("Exception during equals: " + t);
                    // ignore?
                }
                if (object instanceof Comparable<?>) {
                    // TODO
                }
            }

            trace.addEntry(statement.getPosition(), var, entry);
        } catch (CodeUnderTestException e) {
            logger.debug("", e);
        }

    }

    @Override
    public void testExecutionFinished(ExecutionResult r, Scope s) {
        // do nothing
    }
}
