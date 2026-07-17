package presentation.dialogs
import CommonConst.THEME_FUTURE
import CommonConst.THEME_ZABOR

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog // или material если используете M2
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ThemeDialog(
 select: String = THEME_FUTURE,
 listAction: List<String>,
 onClick : (String)-> Unit = {},
 onCancel : () -> Unit = {}){
 
   var selected by remember { mutableStateOf(select) }
   
  AlertDialog(
    onDismissRequest = { onCancel() },
    confirmButton = {
                TextButton(onClick = {
                  onClick(selected)
                }) {
                    Text("Ок")
                }
            },
    dismissButton = {
                TextButton(onClick = { onCancel() }) {
                    Text("Отмена")
                }
            },

    text = {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
      Text("Выберите тему")
                Column(Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(10.dp) ) {
                    listAction.forEach {text ->
                        Row( Modifier.fillMaxWidth()
                            .selectable(
                                selected = (text == selected),
                            
                                onClick = { selected = text }),
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            RadioButton(
                                selected = (text == selected),
                               
                                onClick = null 
                            )
                            Text( text = text, fontSize = 24.sp )
                        }
                    }
                }
                
    }
      
    }
)
  
}
