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

import jakarta.validation.constraints.NotNull;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * Utility methods for URI encoding and decoding based on RFC 3986.
 *
 * <p>There are two types of encode methods:
 * <ul>
 * <li>{@code "encodeXyz"} -- these encode a specific URI component (e.g. path,
 * query) by percent encoding illegal characters, which includes non-US-ASCII
 * characters, and also characters that are otherwise illegal within the given
 * URI component type, as defined in RFC 3986. The effect of this method, with
 * regards to encoding, is comparable to using the multi-argument constructor
 * of {@link URI}.
 * <li>{@code "encode"} and {@code "encodeUriVariables"} -- these can be used
 * to encode URI variable values by percent encoding all characters that are
 * either illegal, or have any reserved meaning, anywhere within a URI.
 * </ul>
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.0
 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>
 */
public class UriUtils {

	/**
	 * Enumeration used to identify the allowed characters per URI component.
	 * <p>Contains methods to indicate whether a given character is valid in a specific URI component.
	 * @see <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>
	 */
	private enum Type {

		PATH {
			public boolean isAllowed(int c) {
				return isPchar(c) || '/' == c;
			}
		};

		/**
		 * Indicates whether the given character is allowed in this URI component.
		 * @return {@code true} if the character is allowed; {@code false} otherwise
		 */
		public abstract boolean isAllowed(int c);

		/**
		 * Indicates whether the given character is in the {@code ALPHA} set.
		 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
		 */
		protected boolean isAlpha(int c) {
			return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
		}

		/**
		 * Indicates whether the given character is in the {@code DIGIT} set.
		 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
		 */
		protected boolean isDigit(int c) {
			return (c >= '0' && c <= '9');
		}

		/**
		 * Indicates whether the given character is in the {@code sub-delims} set.
		 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
		 */
		protected boolean isSubDelimiter(int c) {
			return ('!' == c || '$' == c || '&' == c || '\'' == c || '(' == c || ')' == c || '*' == c || '+' == c ||
					',' == c || ';' == c || '=' == c);
		}

		/**
		 * Indicates whether the given character is in the {@code unreserved} set.
		 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
		 */
		protected boolean isUnreserved(int c) {
			return (isAlpha(c) || isDigit(c) || '-' == c || '.' == c || '_' == c || '~' == c);
		}

		/**
		 * Indicates whether the given character is in the {@code pchar} set.
		 * @see <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
		 */
		protected boolean isPchar(int c) {
			return (isUnreserved(c) || isSubDelimiter(c) || ':' == c || '@' == c);
		}
	}

	private UriUtils() {}

	/**
	 * Encode the given URI path with the given encoding.
	 * @param path the path to be encoded
	 * @param charset the character encoding to encode to
	 * @return the encoded path
	 * @since 5.0
	 */
	public static String encodePath(String path, Charset charset) {
		return encodeUriComponent(path, charset, Type.PATH);
	}

	/**
	 * Encode the given source into an encoded String using the rules specified
	 * by the given component and with the given options.
	 * @param source the source String
	 * @param charset the encoding of the source String
	 * @param type the URI component for the source
	 * @return the encoded URI
	 * @throws IllegalArgumentException when the given value is not a valid URI component
	 */
	private static String encodeUriComponent(String source, @NotNull Charset charset, @NotNull Type type) {
		if (source == null || source.isEmpty()) {
			return source;
		}

		var bytes = source.getBytes(charset);
		var original = true;
		for (var b : bytes) {
			if (!type.isAllowed(b)) {
				original = false;
				break;
			}
		}
		if (original) {
			return source;
		}

		var baos = new ByteArrayOutputStream(bytes.length);
		for (var b : bytes) {
			if (type.isAllowed(b)) {
				baos.write(b);
			}
			else {
				baos.write('%');
				var hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				var hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				baos.write(hex1);
				baos.write(hex2);
			}
		}
		return baos.toString(charset);
	}
}