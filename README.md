# Material-ViewPagerIndicator

[![API](https://img.shields.io/badge/API-11%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=11)
[![GitHub license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/ronaldsmartin/Material-ViewPagerIndicator/blob/master/LICENSE.md)

A page indicator for the [Android Support Library v4's](https://developer.android.com/topic/libraries/support-library/features.html#v4-core-ui) [`ViewPager`](https://developer.android.com/reference/android/support/v4/view/ViewPager.html) widget with Material Design ink animations.

## Usage

`ViewPagerIndicator` is a [`@ViewPager.DecorView`](https://developer.android.com/reference/android/support/v4/view/ViewPager.DecorView.html) (like [Support-Design](https://developer.android.com/topic/libraries/support-library/features.html#design)'s [`TabLayout`](https://developer.android.com/reference/android/support/design/widget/TabLayout.html) and Support-v4's [`PagerTabStrip`](https://developer.android.com/reference/android/support/v4/view/PagerTabStrip.html) and [`PagerTitleStrip`](https://developer.android.com/reference/android/support/v4/view/PagerTitleStrip.html)) widgets.

Usage is simple - just add it as a child view of your `ViewPager`!

### XML

```xml
<android.support.v4.view.ViewPager
    android:id="@+id/view_pager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_alignWithParentIfMissing="true">

    <!-- Add as a direct child of your ViewPager -->
    <com.itsronald.widget.ViewPagerIndicator
        android:id="@+id/view_pager_indicator"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|center_horizontal"
        android:gravity="center_vertical"/>

</android.support.v4.view.ViewPager>
```

### Programatically
Or in code...

```java
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import com.itsronald.widget.ViewPagerIndicator;

...

ViewPager viewPager = ...;

final ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
layoutParams.width = LayoutParams.MATCH_PARENT;
layoutParams.height = LayoutParams.WRAP_CONTENT;
layoutParams.gravity = Gravity.BOTTOM;

final ViewPagerIndicator viewPagerIndicator = new ViewPagerIndicator(context);
viewPager.addView(viewPagerIndicator, layoutParams);

```

## License

**Material-ViewPagerIndicator** is licensed under Apache 2.0.

    Copyright 2016 Ronald Martin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
Parts of this library are derived from Support-v4's [`PagerTitleStrip`](https://android.googlesource.com/platform/frameworks/support.git/+/master/v4/java/android/support/v4/view/PagerTitleStrip.java), which is also licensed under Apache 2.0.
