package com.prestutti.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val color: Color
)

private val pages = listOf(
    OnboardingPage(
        emoji = "👋",
        title = "Bienvenido a Prestutti",
        description = "Tu memoria externa para préstamos. Nunca más pierdas de vista lo que prestás o te prestan.",
        color = PrestuttiPurple
    ),
    OnboardingPage(
        emoji = "📦",
        title = "Registrá lo que prestás",
        description = "Guardá el ítem, a quién se lo prestaste y cuándo te lo devuelven. Todo en un solo lugar.",
        color = PrestuttiPurple
    ),
    OnboardingPage(
        emoji = "🤝",
        title = "Controlá lo que te prestan",
        description = "Llevá el registro de las cosas que te prestaron para no olvidar devolverlas a tiempo.",
        color = PrestuttiPink
    )
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex
    val currentColor = pages[pagerState.currentPage].color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón saltar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!isLastPage) {
                TextButton(onClick = {
                    viewModel.completeOnboarding()
                    onNavigateToLogin()
                }) {
                    Text("Saltar", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        // Pager
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
        }

        // Indicadores de página
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            pages.indices.forEach { index ->
                val width by animateDpAsState(
                    targetValue = if (pagerState.currentPage == index) 24.dp else 8.dp,
                    label = "indicator_width"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) currentColor
                            else Color.LightGray
                        )
                )
            }
        }

        // Botón siguiente / empezar
        Button(
            onClick = {
                if (isLastPage) {
                    viewModel.completeOnboarding()
                    onNavigateToLogin()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = currentColor)
        ) {
            Text(
                text = if (isLastPage) "¡Empezar!" else "Siguiente",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji grande
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(page.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.emoji, fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = page.color,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
