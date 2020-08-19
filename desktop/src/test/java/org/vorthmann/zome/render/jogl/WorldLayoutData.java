/*
 * Copyright 2014 Google Inc. All Rights Reserved.

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

package org.vorthmann.zome.render.jogl;

/**
 * Created by cjr on 6/18/14.
 */
public final class WorldLayoutData {

    public static final float[] FLOOR_COORDS = new float[] {
            200f, -10, -200f,
            -200f, -10, -200f,
            -200f, -10, 200f,
            200f, -10, -200f,
            -200f, -10, 200f,
            200f, -10, 200f,
    };

    public static final float[] FLOOR_NORMALS = new float[] {
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };

    public static final float[] FLOOR_COLOR = new float[] {
            0.05f, 0.6f, 0.2f, 1.0f,
    };

}
