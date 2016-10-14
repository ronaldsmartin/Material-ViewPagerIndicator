/*
 * Copyright (C) 2016 Ronald Martin <hello@itsronald.com>
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
 *
 * Last modified 10/13/16 11:34 AM.
 */

package com.itsronald.materialviewpagerindicatorsample;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean simpleXmlCardExpanded = false;
    private boolean simpleJavacardExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //region OnClick methods

    //region Simple XML Example

    public void onSimpleXmlExampleCardClick(View view) {
        final Intent intent = new Intent(this, SimpleXmlExampleActivity.class);
        startActivity(intent);
    }

    public void onSimpleXmlExampleActionViewActivity(View view) {

    }

    public void onSimpleXmlExampleActionViewLayout(View view) {

    }

    public void onSimpleXmlExampleToggleActionClicked(View view) {
        final ImageButton imageButton = (ImageButton) view;
        toggleExpandButtonImage(imageButton, simpleXmlCardExpanded);

        final TextView description = (TextView) findViewById(R.id.text_simple_xml_description);
        toggleTextExpanded(description, simpleXmlCardExpanded);

        simpleXmlCardExpanded = !simpleXmlCardExpanded;
    }

    private void toggleExpandButtonImage(ImageButton button, boolean isExpanded) {
        final int newDrawableId = isExpanded ?
                R.drawable.ic_expand_more_black_24dp : R.drawable.ic_expand_less_black_24dp;
        final Drawable newDrawable = AppCompatResources.getDrawable(this, newDrawableId);
        button.setImageDrawable(newDrawable);
    }

    private void toggleTextExpanded(TextView textView, boolean isExpanded) {
        final int newVisibility = isExpanded ? TextView.GONE : TextView.VISIBLE;
        textView.setVisibility(newVisibility);
    }

    //endregion

    //region Simple Java Example

    public void onSimpleJavaExampleCardClick(View view) {
        final Intent intent = new Intent(this, SimpleJavaExampleActivity.class);
        startActivity(intent);
    }

    public void onSimpleJavaExampleActionViewActivity(View view) {

    }

    public void onSimpleJavaExampleActionViewLayout(View view) {

    }

    public void onSimpleJavaExampleToggleActionClicked(View view) {
        final ImageButton imageButton = (ImageButton) view;
        toggleExpandButtonImage(imageButton, simpleJavacardExpanded);

        final TextView description = (TextView) findViewById(R.id.text_simple_java_description);
        toggleTextExpanded(description, simpleJavacardExpanded);

        simpleJavacardExpanded = !simpleJavacardExpanded;
    }

    //endregion

    public void onFABClick(View view) {
        final String codeRepoUrl = getString(R.string.repo_url);
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(codeRepoUrl));
        startActivity(intent);
    }

    //endregion
}
