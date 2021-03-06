/*
 * Copyright 2020 Marc Donald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marcdonald.hibi.internal.base

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.marcdonald.hibi.HibiAndroidViewModelFactory
import com.marcdonald.hibi.HibiViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

abstract class HibiBottomSheetDialogFragment : BottomSheetDialogFragment(), KodeinAware {
	override val kodein: Kodein by closestKodein()
	protected val viewModelFactory: HibiViewModelFactory by instance()
	protected val androidViewModelFactory: HibiAndroidViewModelFactory by instance()
}