package presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun Notebook(){
    
        // Переменная для хранения введенного текста
    var text by remember { mutableStateOf("") }

   
        OutlinedTextField(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            value = text,
            onValueChange = { newText -> text = newText }, // Обновляем стейт при вводе
            shape = RoundedCornerShape(16.dp),
            
        )

    
}
