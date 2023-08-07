/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * (the original source has been stripped down and modified to suit the needs of this project)
 */

package be.vlaanderen.vip.mock.magda.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Med Belamachi
 */
class UriUtilsTest {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	@Test
	void encodePath() {
		assertThat(UriUtils.encodePath("/foo/bar", CHARSET)).as("Invalid encoded result").isEqualTo("/foo/bar");
		assertThat(UriUtils.encodePath("/foo bar", CHARSET)).as("Invalid encoded result").isEqualTo("/foo%20bar");
		assertThat(UriUtils.encodePath("/Z\u00fcrich", CHARSET)).as("Invalid encoded result").isEqualTo("/Z%C3%BCrich");
	}
}