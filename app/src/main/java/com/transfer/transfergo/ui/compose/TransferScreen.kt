package com.transfer.transfergo.ui.compose

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.transfer.domain.model.Currency
import com.transfer.transfergo.R
import com.transfer.transfergo.TransferConract.Event
import com.transfer.transfergo.TransferConract.State

@Composable
fun TransferScreen(
    state: State,
    onEvent: (Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Sending from",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CurrencySelector(
                                currency = state.fromCurrency,
                                onCurrencySelected = { onEvent(Event.OnFromCurrencySelected(it)) }
                            )

                            var textFieldValue by remember(state.amount) { mutableStateOf(state.amount.toString()) }

                            BasicTextField(
                                value = textFieldValue,
                                onValueChange = {
                                    textFieldValue = it
                                    val newVal = it.toDoubleOrNull() ?: 0.0
                                    onEvent(Event.OnAmountChanged(newVal))
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color(0xFF007BFF),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F4F9))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Receiver gets",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CurrencySelector(
                                currency = state.toCurrency,
                                onCurrencySelected = { onEvent(Event.OnToCurrencySelected(it)) }
                            )

                            val resultDisplay = state.resultText.split(" ").firstOrNull() ?: "0.00"
                            Text(
                                text = resultDisplay,
                                color = Color.Black,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 40.dp, y = 0.dp)
                    .size(36.dp)
                    .zIndex(1f)
                    .clickable { onEvent(Event.OnSwapClicked) },
                shape = CircleShape,
                color = Color(0xFF007BFF),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_reverse__button),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                    )
                }
            }

            val rateDisplay = state.resultText.substringAfter("@ ").trim()
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 0.dp)
                    .zIndex(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black
            ) {
                Text(
                    text = "1 ${state.fromCurrency.code} = $rateDisplay ${state.toCurrency.code}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CurrencySelector(
    currency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = getFlagResource(currency)),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currency.code,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(2.dp))
            Image(
                painter = painterResource(id = R.drawable.icon_arrow_down),
                contentDescription = null,
                modifier = Modifier.size(8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Currency.entries.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = getFlagResource(currency)),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = currency.code)
                        }
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getFlagResource(currency: Currency): Int {
    return when (currency) {
        Currency.PLN -> R.drawable.icon_poland
        Currency.EUR -> R.drawable.icon_germany
        Currency.GBP -> R.drawable.icon_great_britain
        Currency.UAH -> R.drawable.icon_ukraine
    }
}

@Preview(showBackground = true)
@Composable
fun TransferScreenPreview() {
    val state = State(
        fromCurrency = Currency.PLN,
        toCurrency = Currency.UAH,
        amount = 100.0,
        resultText = "723.38 UAH @ 7.23"
    )

    MaterialTheme {
        TransferScreen(
            state = state,
            onEvent = { }
        )
    }
}
