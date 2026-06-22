package com.example.seapedia.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seapedia.R
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import com.example.seapedia.core.ui.theme.SeaPrimary
import com.example.seapedia.core.ui.theme.Surface
import com.example.seapedia.core.ui.theme.OnSurfaceVariant
import com.example.seapedia.core.ui.theme.OutlineVariant
import com.example.seapedia.core.ui.theme.SeaTertiary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { HomeTopAppBar() },
        bottomBar = { HomeBottomNavigation() },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { SearchBarSection(modifier = Modifier.padding(16.dp)) }
            item { HeroBanner(modifier = Modifier.padding(horizontal = 16.dp)) }
            item { CategoriesSection(modifier = Modifier.padding(vertical = 24.dp)) }
            item { FeaturedProductsSection(modifier = Modifier.padding(horizontal = 16.dp)) }
            item { TestimonialsSection(modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
fun HomeTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_menu),
            contentDescription = "Menu",
            tint = SeaPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SeaPrimary,
                letterSpacing = 2.sp
            )
        )
        Icon(
            painter = painterResource(R.drawable.ic_notifications),
            contentDescription = "Notifications",
            tint = SeaPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SearchBarSection(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                tint = OnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        },
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = OutlineVariant.copy(alpha = 0.5f),
            focusedBorderColor = SeaPrimary,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        singleLine = true,
        readOnly = true
    )
}

@Composable
fun HeroBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SeaPrimary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
            )
            
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = stringResource(R.string.hero_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier.width(220.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.hero_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CategoriesSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.categories_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryItem(stringResource(R.string.electronics), "💻")
            CategoryItem(stringResource(R.string.home_living), "🏠")
            CategoryItem(stringResource(R.string.fashion), "👕")
            CategoryItem(stringResource(R.string.groceries), "🍎")
        }
    }
}

@Composable
fun CategoryItem(label: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = OnSurfaceVariant,
            maxLines = 2,
            minLines = 2
        )
    }
}

@Composable
fun FeaturedProductsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.featured_products),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SeaPrimary
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProductCard(
                    modifier = Modifier.weight(1f),
                    category = "Electronics",
                    name = "Premium Wireless Headphones",
                    price = "$199.99"
                )
                ProductCard(
                    modifier = Modifier.weight(1f),
                    category = "Appliances",
                    name = "Smart Power Blender",
                    price = "$89.90"
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ProductCard(
                    modifier = Modifier.weight(1f),
                    category = "Electronics",
                    name = "Minimalist Smart Watch V2",
                    price = "$149.00"
                )
                ProductCard(
                    modifier = Modifier.weight(1f),
                    category = "Electronics",
                    name = "Smartphone Pro Max",
                    price = "$999.00"
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    category: String,
    name: String,
    price: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = ColorPainter(Color.Gray),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SeaPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun TestimonialsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.testimonials_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SeaTertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JD",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(5) {
                                Text("★", color = Color(0xFFFFB300), fontSize = 14.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"SEAPEDIA completely changed how I find suppliers. The interface is clean, and the transactions are seamless!\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        color = Color(0xFF333333)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- Jane Doe, Business Owner",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SeaPrimary),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_orders), // Use a suitable icon
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.leave_review),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = stringResource(R.string.home_nav)
                )
            },
            label = { Text(stringResource(R.string.home_nav)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SeaPrimary,
                selectedTextColor = SeaPrimary,
                indicatorColor = SeaPrimary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_orders),
                    contentDescription = stringResource(R.string.orders_nav)
                )
            },
            label = { Text(stringResource(R.string.orders_nav)) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = OnSurfaceVariant,
                unselectedTextColor = OnSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_nav)
                )
            },
            label = { Text(stringResource(R.string.search_nav)) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = OnSurfaceVariant,
                unselectedTextColor = OnSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = stringResource(R.string.profile_nav)
                )
            },
            label = { Text(stringResource(R.string.profile_nav)) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = OnSurfaceVariant,
                unselectedTextColor = OnSurfaceVariant
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SeaPediaTheme(dynamicColor = false) {
        HomeScreen()
    }
}
