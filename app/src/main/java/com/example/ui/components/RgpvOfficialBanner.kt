package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Authentic RGPV Header Banner replicating the exact official university banner:
 * - Pitch-black background (#000000)
 * - Official red RGPV Emblem logo (ic_rgpv_logo)
 * - Headline: "Rajiv Gandhi Proudyogiki Vishwavidyalaya (RGPV)"
 * - Subtitle: "(State Technological University of M.P)"
 */
@Composable
fun RgpvOfficialBanner(
    modifier: Modifier = Modifier,
    showLocationSubtitle: Boolean = true,
    showStatusBadge: Boolean = true,
    logoSize: Dp = 50.dp,
    compact: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (compact) 12.dp else 18.dp))
            .background(Color(0xFF000000))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(if (compact) 12.dp else 18.dp))
            .padding(horizontal = if (compact) 12.dp else 16.dp, vertical = if (compact) 10.dp else 14.dp)
            .testTag("rgpv_official_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Official Emblem Seal
            Box(
                modifier = Modifier
                    .size(logoSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFC62828), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rgpv_logo),
                    contentDescription = "RGPV Official Emblem",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(logoSize)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(if (compact) 10.dp else 14.dp))

            // Official Typography
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rajiv Gandhi Proudyogiki Vishwavidyalaya (RGPV)",
                    color = Color(0xFFFFFFFF),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 13.sp else 15.sp,
                    letterSpacing = 0.2.sp,
                    lineHeight = if (compact) 16.sp else 19.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "(State Technological University of M.P)",
                    color = Color(0xFFB0B0B0),
                    fontWeight = FontWeight.Medium,
                    fontSize = if (compact) 10.sp else 11.sp,
                    letterSpacing = 0.1.sp
                )

                if (showLocationSubtitle && !compact) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Airport Bypass Road, Gandhi Nagar, Bhopal - 462033",
                        color = Color(0xFF888888),
                        fontSize = 9.5.sp
                    )
                }
            }

            if (showStatusBadge && !compact) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFICIAL",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "PORTAL",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
