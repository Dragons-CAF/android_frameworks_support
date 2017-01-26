/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.support.room.solver.query.result

import com.android.support.room.processor.Context
import com.android.support.room.solver.CodeGenScope
import com.android.support.room.solver.types.ColumnTypeAdapter
import javax.lang.model.element.Element

/**
 * Wraps a row adapter when there is only 1 item  with 1 column in the response.
 */
class SingleColumnRowAdapter(val adapter : ColumnTypeAdapter) : RowAdapter(adapter.out) {
    override fun reportErrors(context: Context, element: Element, suppressedWarnings: Set<String>) {
        // we just assume it matches so no errors to report
    }

    override fun init(cursorVarName: String, scope: CodeGenScope) : RowConverter {
        return object : RowConverter {
            override fun convert(outVarName: String, cursorVarName: String) {
                adapter.readFromCursor(outVarName, cursorVarName, "0", scope)
            }
        }
    }
}
