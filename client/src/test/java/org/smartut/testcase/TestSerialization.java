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
package org.smartut.testcase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.smartut.testcase.statements.PrimitiveStatement;
import org.junit.Assert;
import org.junit.Test;


public class TestSerialization {
	
	@Test
	public void testSerializationEmptyTest() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		double fitness = 3.14;
		TestChromosome chromosome = new TestChromosome();
		chromosome.setFitness(null, fitness);		
		oos.writeObject(chromosome);
		byte [] baSerialized = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(baSerialized);
		ObjectInputStream ois = new ObjectInputStream(bais);
		TestChromosome copy = (TestChromosome) ois.readObject();
		Assert.assertEquals(chromosome.getFitness(), copy.getFitness(), 0.0);
	}
	
	@Test
	public void testSerializationNonEmptyTest() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		double fitness = 0.9950513142057124d;
		TestChromosome chromosome = new TestChromosome();
		TestCase test = new DefaultTestCase();
		PrimitiveStatement<?> statement = PrimitiveStatement.getPrimitiveStatement(test, int.class);
		test.addStatement(statement);
		chromosome.setTestCase(test);
		chromosome.setFitness(null, fitness);		
		oos.writeObject(chromosome);
		byte [] baSerialized = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(baSerialized);
		ObjectInputStream ois = new ObjectInputStream(bais);
		TestChromosome copy = (TestChromosome) ois.readObject();
		Assert.assertEquals(chromosome.getFitness(), copy.getFitness(), 0.0);
	}

}
