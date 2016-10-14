# ![Icon](https://github.com/ronaldsmartin/Material-ViewPagerIndicator/blob/master/app/src/main/res/mipmap-mdpi/ic_launcher.png) Material-ViewPagerIndicator

[![Release](https://jitpack.io/v/ronaldsmartin/Material-ViewPagerIndicator.svg)](https://jitpack.io/#ronaldsmartin/Material-ViewPagerIndicator)
[![API 11+](https://img.shields.io/badge/API-11%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=11)
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/ronaldsmartin/Material-ViewPagerIndicator/blob/master/LICENSE.md)
[![Build Status](https://travis-ci.org/ronaldsmartin/Material-ViewPagerIndicator.svg?branch=master)](https://travis-ci.org/ronaldsmartin/Material-ViewPagerIndicator)

A super easy-to-use page indicator for the [Android Support Library v4's](https://developer.android.com/topic/libraries/support-library/features.html#v4-core-ui) [`ViewPager`](https://developer.android.com/reference/android/support/v4/view/ViewPager.html) widget with [Material Design](https://material.google.com/motion/material-motion.html#) ink animations.

![Example screen capture](https://raw.githubusercontent.com/ronaldsmartin/Material-ViewPagerIndicator/assets/screenshots/1.0.0/capture-v1.0.0.gif)

### Demo

A demo app with examples is available on Google Play. The source for the demo is the `app` module in this project.

[<img alt='Get it on Google Play' 
    src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'
    width="300"/>](https://play.google.com/store/apps/details?id=com.itsronald.materialviewpagerindicatorsample&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1)

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

For more advanced usage, see [the wiki](https://github.com/ronaldsmartin/Material-ViewPagerIndicator/wiki/Advanced-Usage).

## Download
**Material-ViewPagerIndicator** is available [via JitPack.io](https://jitpack.io/#ronaldsmartin/Material-ViewPagerIndicator).

### Gradle

Add JitPack to your maven repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Add Material-ViewPagerIndicator to your app dependencies:

```groovy

dependencies {
    // x.y.z is the latest release version number.
    compile 'com.github.ronaldsmartin:Material-ViewPagerIndicator:x.y.z'
}

```

### Other

Please see the [JitPack page](https://jitpack.io/#ronaldsmartin/Material-ViewPagerIndicator) for instructions on using the library with other build systems. 

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
