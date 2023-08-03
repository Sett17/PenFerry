package dev.dikka.penferry

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Output(
    private val list: SnapshotStateList<String>,
    private val state: LazyListState,
    private val scope: CoroutineScope
) {

    operator fun plus(s: Any) {
        Log.d("OUTPUT", s.toString())
        MainScope().launch {
            list.add(s.toString())
            scope.launch {
                state.scrollToItem(list.lastIndex)
            }
        }
    }

    @Composable
    fun New(modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
        Box {
            LazyColumn(
                modifier,
                state = state,
            ) {
                items(list) {
                    Text(it, fontSize = fontSize)
                }
            }
        }
    }
}