/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.ui.auth.ui.email;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.auth.R;
import com.firebase.ui.auth.ui.ActivityHelper;
import com.firebase.ui.auth.ui.AppCompatBase;
import com.firebase.ui.auth.ui.ExtraConstants;
import com.firebase.ui.auth.ui.FlowParameters;

public class ConfirmRecoverPasswordActivity extends android.support.v7.app.AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = ConfirmRecoverPasswordActivity.class.getName()+": " ;
    private ActivityHelper mActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHelper = new ActivityHelper(this, getIntent());
        // intentionally do not configure the theme on this activity, it is a dialog

        Log.v(TAG, " inside on create");

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.color_gradient_top));
        }

        setContentView(R.layout.confirm_recovery_layout);
        String email = getIntent().getStringExtra(ExtraConstants.EXTRA_EMAIL);
        boolean isSuccess = getIntent().getBooleanExtra(ExtraConstants.EXTRA_SUCCESS, true);

        if (isSuccess) {
            String text = String.format(
                    getResources().getString(R.string.confirm_recovery_body),
                    email
            );
            ((TextView) findViewById(R.id.body_text)).setText(text);
        } else {
            ((TextView) findViewById(R.id.body_text)).setText(R.string.recovery_fail_body);
        }
        findViewById(R.id.button_done).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_done) {
            finish(RESULT_OK, new Intent());
        }
    }

    public static Intent createIntent(
            Context context,
            FlowParameters flowParams,
            boolean success,
            String email) {
        return ActivityHelper.createBaseIntent(context, ConfirmRecoverPasswordActivity.class,
                flowParams)
                .putExtra(ExtraConstants.EXTRA_SUCCESS, success)
                .putExtra(ExtraConstants.EXTRA_EMAIL, email);
    }

    private void finish(int resultCode, Intent intent) {
        mActivityHelper.finish(resultCode, intent);
    }
}
