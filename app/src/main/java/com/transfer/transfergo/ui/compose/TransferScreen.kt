package com.transfer.transfergo.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.transfer.domain.model.Currency
import com.transfer.transfergo.R
import com.transfer.transfergo.TransferContract.Event
import com.transfer.transfergo.TransferContract.State

@Composable
fun TransferScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        AnimatedVisibility(visible = state.networkError) {
            InfoPopup(
                title = stringResource(R.string.no_internet_connection_title),
                description = stringResource(R.string.no_internet_connection_message),
                icon = painterResource(id = R.drawable.icon_error),
                onDismiss = { onEvent(Event.OnNetworkErrorDismissed) },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sending_from),
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            CurrencySelector(
                                currency = state.fromCurrency,
                                onCurrencySelected = { onEvent(Event.OnFromCurrencySelected(it)) },
                            )

                            BasicTextField(
                                value = state.fromAmountInput,
                                onValueChange = {
                                    onEvent(Event.OnFromAmountChanged(it))
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = if (state.error) {
                                        colorResource(R.color.secondary)
                                    } else {
                                        colorResource(R.color.primary)
                                    },
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F4F9))
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.receiver_gets),
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            CurrencySelector(
                                currency = state.toCurrency,
                                onCurrencySelected = { onEvent(Event.OnToCurrencySelected(it)) },
                            )

                            Text(
                                text = state.toAmountInput,
                                color = Color.Black,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            SwapButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onEvent(Event.OnSwapClicked) },
            )
            ExchangeRateBadge(
                modifier = Modifier.align(Alignment.Center),
                state = state,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = state.error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFCCCC), RoundedCornerShape(8.dp))
                    .padding(12.dp),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.error_message,
                        formatArgs = arrayOf(
                            state.fromCurrency.limit.toString(),
                            state.fromCurrency.code,
                        ),
                    ),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun ExchangeRateBadge(modifier: Modifier, state: State) {
    val rateDisplay = state.toAmountInput.substringAfter("@ ").trim()

    Surface(
        modifier = modifier
            .offset(y = 0.dp)
            .zIndex(1f),
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.black),
    ) {
        Text(
            text = "1 ${state.fromCurrency.code} = $rateDisplay ${state.toCurrency.code}",
            color = colorResource(R.color.white),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransferScreenPreview() {
    val state = State(
        fromCurrency = Currency.PLN,
        toCurrency = Currency.UAH,
        fromAmountInput = "100.0",
        toAmountInput = "723.38",
    )

    MaterialTheme {
        TransferScreen(
            state = state,
            onEvent = { },
        )
    }
}
