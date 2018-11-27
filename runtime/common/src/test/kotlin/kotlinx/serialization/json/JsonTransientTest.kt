/*
 * Copyright 2017-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */
@file:Suppress("EqualsOrHashCode")

package kotlinx.serialization.json

import kotlinx.serialization.*
import kotlin.test.*

class JsonTransientTest : JsonTestBase() {

    @Serializable
    class Data(val a: Int = 0, @Transient var b: Int = 42, @Optional val e: Boolean = false) {
        @Optional
        var c = "Hello"

        @Transient
        val d: String
            get() = "hello"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Data

            if (a != other.a) return false
            if (b != other.b) return false
            if (c != other.c) return false
            if (d != other.d) return false

            return true
        }

        override fun toString(): String {
            return "Data(a=$a, b=$b, e=$e, c='$c', d='$d')"
        }
    }

    @Test
    fun testAll() = parametrizedTest { useStreaming ->
        assertEquals("{a:0,e:false,c:Hello}", unquoted.stringify(Data(), useStreaming))
    }

    @Test
    fun testMissingOptionals() = parametrizedTest { useStreaming ->
        assertEquals(strict.parse("{a:0,c:Hello}", useStreaming), Data())
        assertEquals(strict.parse("{a:0}", useStreaming), Data())
    }

    @Test
    fun testThrowTransient() = parametrizedTest { useStreaming ->
        assertFailsWith(JsonUnknownKeyException::class) {
            strict.parse<Data>("{a:0,b:100500,c:Hello}", useStreaming)
        }
    }
}
