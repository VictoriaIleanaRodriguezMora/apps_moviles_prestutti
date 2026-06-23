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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.prestutti.R
import com.prestutti.ui.theme.FondoGris
import com.prestutti.ui.theme.PrestuttiPink
import com.prestutti.ui.theme.PrestuttiPurple
import com.prestutti.ui.theme.PrestuttiPurpleOnboarding
import kotlinx.coroutines.launch

// ── Modelos ───────────────────────────────────────────────────────────────────

sealed class OnboardingSlide {
    // representa los distintos tipos de página. Tiene dos variantes:

    // Pantalla con contenido
    data class Content(
        val emoji: String,
        val title: String,
        val description: String,
        val color: Color
    ) : OnboardingSlide()

    // Pantalla de imagen
    data class ImageSlide(val imageRes: Int) : OnboardingSlide()
}

private val slides: List<OnboardingSlide> = listOf(
    // Pantallas de contenido
    OnboardingSlide.Content(
        emoji = "👋",
        title = "Bienvenido a Prestutti",
        description = "Tu memoria externa para préstamos. Nunca más pierdas de vista lo que prestás o te prestan.",
        color = PrestuttiPurpleOnboarding
    ),
    OnboardingSlide.Content(
        emoji = "📦",
        title = "Registrá lo que prestás",
        description = "Guardá el ítem, a quién se lo prestaste y cuándo te lo devuelven. Todo en un solo lugar.",
        color = PrestuttiPurpleOnboarding
    ),
    OnboardingSlide.Content(
        emoji = "🤝",
        title = "Controlá lo que te prestan",
        description = "Llevá el registro de las cosas que te prestaron para no olvidar devolverlas a tiempo.",
        color = PrestuttiPurpleOnboarding
    ),
    // Pantallas con imágenes
    OnboardingSlide.ImageSlide(R.drawable.onboarding_1),
    OnboardingSlide.ImageSlide(R.drawable.onboarding_2),
    OnboardingSlide.ImageSlide(R.drawable.onboarding_3),
    OnboardingSlide.ImageSlide(R.drawable.onboarding_4)
)

// ── Pantalla principal ────────────────────────────────────────────────────────

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    // Este callback sirve para navegar al Login cuando termina el onboarding
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState() // deslizar páginas
    // pagerState.currentPage devuelve: 0, 1, 2 según dónde esté el usuario
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == slides.lastIndex // sólo en la última
    val currentSlide = slides[pagerState.currentPage]
    val accentColor = when (currentSlide) {
        is OnboardingSlide.Content -> currentSlide.color
        is OnboardingSlide.ImageSlide -> PrestuttiPurple
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── Pager ─────────────────────────────────────────────────
        // se muestran las páginas
        HorizontalPager(
            count = slides.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { pageIndex ->
            when (val slide = slides[pageIndex]) {
                // Si es Content, llama a ContentSlide
                is OnboardingSlide.Content -> ContentSlide(slide)
                // Si es ImageSlide llama a ImageSlide
                is OnboardingSlide.ImageSlide -> ImageSlide(slide)
            }
        }

        // ── Controles superpuestos ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicadores de página
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slides.indices.forEach { index ->
                    // animacion de transicion
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
                                if (pagerState.currentPage == index) accentColor
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
                // botón saltear. Solo aparece si NO estoy en la última página.
                if (!isLastPage) {
                    OutlinedButton(
                        onClick = {
                            // onboarding_completed = true en DataStore
                            viewModel.completeOnboarding()
                            onNavigateToLogin()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                    ) {
                        Text("Saltar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                // Botón Siguiente
                Button(
                    onClick = {
                        if (isLastPage) {
                            // si es la ultima, marca el onboarding completado y viaja a login
                            viewModel.completeOnboarding()
                            onNavigateToLogin()
                        } else {
                            // Si NO es la última página, avanza a la siguiente
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = if (isLastPage) "¡Empezar!" else "Siguiente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

// ── Slide de contenido Compose ────────────────────────────────────────────────

@Composable
private fun ContentSlide(slide: OnboardingSlide.Content) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGris)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(slide.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = slide.emoji, fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = slide.title,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = slide.color,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = slide.description,
            fontSize = 30.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        // Espaciado para que los botones no tapen el contenido
        Spacer(modifier = Modifier.height(120.dp))
    }
}

// ── Slide de imagen ───────────────────────────────────────────────────────────
// Carga la imagen del drawable
@Composable
private fun ImageSlide(slide: OnboardingSlide.ImageSlide) {
    Image(
        painter = painterResource(id = slide.imageRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )
}
