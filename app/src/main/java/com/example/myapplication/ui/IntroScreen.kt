package com.example.ocrscanner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

@Composable
fun IntroScreen(onStartScan: () -> Unit) {
    val customFont = FontFamily(
        Font(
            resId = R.font.ibm_plex_sans_semi_bold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_scanner),
                contentDescription = "Scanner Icon",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartScan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6300),
                    contentColor = Color(0xFF851600)
                )
            ) {
                Text(
                    text = "Scan Thread",
                    fontSize = 18.sp,
                    fontFamily = customFont
                )
            }
        }
    }
}
