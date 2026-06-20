package com.prestutti.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.prestutti.R
import com.prestutti.ui.theme.PrestuttiPurple
import kotlinx.coroutines.launch

private val onboardingImages = listOf(
    R.drawable.onboarding_1,
    R.drawable.onboarding_2,
    R.drawable.onboarding_3,
    R.drawable.onboarding_4
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingImages.lastIndex

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Imágenes deslizables ──────────────────────────────────────────────
        HorizontalPager(
            count = onboardingImages.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { pageIndex ->
            Image(
                painter = painterResource(id = onboardingImages[pageIndex]),
                contentDescription = "Onboarding ${pageIndex + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Controles superpuestos abajo ──────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicadores de página
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onboardingImages.indices.forEach { index ->
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
                                if (pagerState.currentPage == index) PrestuttiPurple
                                else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            // Botones
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                // Botón saltar (visible en todas menos la última)
                if (!isLastPage) {
                    OutlinedButton(
                        onClick = {
                            viewModel.completeOnboarding()
                            onNavigateToLogin()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                    ) {
                        Text("Saltar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrestuttiPurple)
                ) {
                    Text(
                        text = if (isLastPage) "¡Empezar!" else "Siguiente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
