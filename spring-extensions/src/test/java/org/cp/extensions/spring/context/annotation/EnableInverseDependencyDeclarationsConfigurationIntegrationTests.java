/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.extensions.spring.context.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for the {@link EnableInverseDependencyDeclarations} annotation
 * and the {@link InverseDependencyDeclarationsConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.cp.extensions.spring.context.annotation.EnableInverseDependencyDeclarations
 * @see org.cp.extensions.spring.context.annotation.InverseDependencyDeclarationsConfiguration
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 0.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class EnableInverseDependencyDeclarationsConfigurationIntegrationTests {

	private static final List<String> beanNames = new CopyOnWriteArrayList<>();

	@Test
	public void beansWereInitializedInDependencyOrder() {
		assertThat(beanNames).containsExactly("C", "A", "B");
	}

	@Configuration
	@EnableInverseDependencyDeclarations
	@SuppressWarnings("unused")
	static class TestConfiguration {

		@Bean
		BeanPostProcessor beanNamesPostProcessor() {

			return new BeanPostProcessor() {

				@Override
				public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

					if (Arrays.asList("A", "B", "C").contains(beanName)) {
						beanNames.add(beanName);
					}

					return bean;
				}
			};
		}

		@Bean("A")
		@DependencyOf("B")
		Object beanOne() {
			return "A";
		}

		@Bean("B")
		Object beanTwo() {
			return "B";
		}

		@Bean("C")
		@DependencyOf("A")
		Object beanThree() {
			return "C";
		}
	}
}
