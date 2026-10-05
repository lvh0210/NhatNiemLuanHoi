package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameLogEntry

@Composable
fun LogChronicleView(
    logs: List<GameLogEntry>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("log_chronicle_view"),
        color = DarkSlate,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HistoryEdu,
                    contentDescription = "Biên Niên Sử",
                    tint = ImmortalGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tu Chân Biên Niên Ký",
                    color = GoldenSun,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${logs.size} sự kiện",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    val tagBgColor = when (log.type) {
                        "SUCCESS" -> JadeGreen.copy(alpha = 0.2f)
                        "WARNING" -> DangerPoison.copy(alpha = 0.2f)
                        "DANGER" -> FatalPoison.copy(alpha = 0.2f)
                        "TRIBULATION" -> LightningPurple.copy(alpha = 0.25f)
                        else -> MysticSurfaceVariant
                    }
                    val tagTextColor = when (log.type) {
                        "SUCCESS" -> JadeGreen
                        "WARNING" -> DangerPoison
                        "DANGER" -> FatalPoison
                        "TRIBULATION" -> LightningPurple
                        else -> SpiritCyan
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "[${log.year}t]",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(tagBgColor)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = log.tag,
                                color = tagTextColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = log.message,
                            color = TextPrimary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
