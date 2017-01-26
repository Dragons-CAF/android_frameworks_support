/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.support.room.processor

import com.android.support.room.RoomWarnings
import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreElements
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.util.SimpleAnnotationValueVisitor6

/**
 * A visitor that reads SuppressWarnings annotations and keeps the ones we know about.
 */
object SuppressWarningProcessor {
    private const val ALL = "ALL"
    private val KNOWN_WARNINGS = setOf(ALL, RoomWarnings.CURSOR_MISMATCH)
    fun getSuppressedWarnings(element: Element): Set<String> {
        val annotation = MoreElements.getAnnotationMirror(element,
                SuppressWarnings::class.java).orNull()
        return if (annotation == null) {
            emptySet<String>()
        } else {
            val value = AnnotationMirrors.getAnnotationValue(annotation, "value")
            if (value == null) {
                emptySet<String>()
            } else {
                VISITOR.visit(value)
            }
        }
    }

    fun isSuppressed(warning: String, suppressedWarnings: Set<String>): Boolean {
        return suppressedWarnings.contains(ALL) || suppressedWarnings.contains(warning)
    }

    private object VISITOR : SimpleAnnotationValueVisitor6<Set<String>, String>() {
        override fun visitArray(values: List<AnnotationValue>?, elementName: String?)
                : Set<String> {
            return values?.map {
                it.value.toString()
            }?.filter {
                KNOWN_WARNINGS.contains(it)
            }?.toSet() ?: emptySet()
        }

        override fun defaultAction(o: Any?, p: String?): Set<String> {
            return emptySet()
        }
    }
}
