/**
 * 
 */
package com.crossover.techtrial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author crossover
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrossRideApplicationTest {
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void applicationContextTest() {
		CrossRideApplication.main(new String[] {});
	}
}
