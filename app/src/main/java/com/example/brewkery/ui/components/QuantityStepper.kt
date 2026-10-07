package com.example.brewkery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.WarmBorder

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, WarmBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "-",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier.clickable(onClick = onDecrement)
            )

            Text(
                text = quantity.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "+",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier.clickable(onClick = onIncrement)
            )
        }
    }
}

@Preview(showBackground = true, name = "Quantity Stepper")
@Composable
fun QuantityStepperPreview() {
    BrewkeryTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuantityStepper(
                quantity = 1,
                onIncrement = {},
                onDecrement = {}
            )
        }
    }
}
