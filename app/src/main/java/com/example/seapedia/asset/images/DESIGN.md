---
name: Oceanic Commerce
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#404850'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#707881'
  outline-variant: '#bfc7d1'
  surface-tint: '#006399'
  primary: '#005d90'
  on-primary: '#ffffff'
  primary-container: '#0077b6'
  on-primary-container: '#f3f7ff'
  inverse-primary: '#94ccff'
  secondary: '#ae2f34'
  on-secondary: '#ffffff'
  secondary-container: '#ff6b6b'
  on-secondary-container: '#6d0010'
  tertiary: '#006176'
  on-tertiary: '#ffffff'
  tertiary-container: '#007c95'
  on-tertiary-container: '#ecf9ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#cde5ff'
  primary-fixed-dim: '#94ccff'
  on-primary-fixed: '#001d32'
  on-primary-fixed-variant: '#004b74'
  secondary-fixed: '#ffdad8'
  secondary-fixed-dim: '#ffb3b0'
  on-secondary-fixed: '#410006'
  on-secondary-fixed-variant: '#8c1520'
  tertiary-fixed: '#b3ebff'
  tertiary-fixed-dim: '#4cd6fb'
  on-tertiary-fixed: '#001f27'
  on-tertiary-fixed-variant: '#004e5f'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  display-lg:
    fontFamily: Montserrat
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Montserrat
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Montserrat
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Montserrat
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  container-max: 1280px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style
The design system is built for a high-velocity, multi-seller ecosystem where clarity and energy are paramount. The brand personality is professional yet vibrant, bridging the gap between a reliable utility and an exciting discovery platform. 

The aesthetic follows a **Corporate / Modern** style with **Glassmorphism** influences for secondary overlays. It prioritizes high legibility and rhythmic spacing to handle dense product data across four distinct user personas. The emotional response should be one of "effortless reliability"—the UI feels sturdy and structured, while the energetic color accents maintain a sense of marketplace momentum.

## Colors
The palette is dominated by **Sea Blue**, used for primary actions and the Buyer experience to evoke trust. **Coral** serves as the energetic accent for notifications, "Hot Deals," and primary CTA highlights.

To manage the multi-role nature of the platform, specific functional hues are assigned:
- **Buyer (Blue):** The default state, focused on exploration and purchasing.
- **Seller (Green):** Applied to dashboards and inventory tools to signify growth and "Go" states.
- **Admin (Purple/Gold):** High-authority zones use a deep purple with gold accents for critical system controls.
- **Driver (Orange):** High-visibility orange for logistics, tracking, and urgent delivery status.

Neutral surfaces utilize a cool gray scale (Slate) to maintain a crisp, clean environment that lets product photography stand out.

## Typography
This design system utilizes a dual-font strategy. **Montserrat** is used for headlines to provide a bold, geometric, and modern character that establishes strong brand presence. **Inter** is used for all body copy, inputs, and UI labels due to its exceptional readability and neutral, systematic feel.

For mobile layouts, headline sizes scale down to prevent excessive line wrapping, while body text remains at a minimum of 16px to ensure accessibility and comfortable reading in high-glare outdoor environments (common for drivers and on-the-go buyers).

## Layout & Spacing
The layout follows a **Fluid Grid** model based on an 8px spatial rhythm. 

- **Mobile:** A 4-column grid with 16px margins. Elements are primarily stacked or use horizontal carousels for product discovery.
- **Tablet:** An 8-column grid with 24px margins. Sidebar navigation often becomes a persistent element for Seller and Admin roles.
- **Desktop:** A 12-column grid with a maximum container width of 1280px. 

Horizontal padding on tap targets is generous (minimum 12px) to accommodate mobile interactions. Vertical stack spacing typically follows a 16px (small), 32px (medium), or 64px (large) sequence.

## Elevation & Depth
This design system uses **Tonal Layers** combined with **Ambient Shadows** to create a sense of organized hierarchy. 

1. **Base (Level 0):** Used for the main background (Slate 50).
2. **Card/Surface (Level 1):** White backgrounds with a very soft, diffused shadow (Y: 2px, Blur: 8px, 4% Opacity Black).
3. **Floating/Active (Level 2):** Used for active navigation items and hover states. Shadows become deeper (Y: 4px, Blur: 12px, 8% Opacity Primary Color).
4. **Overlay/Modal (Level 3):** Used for bottom sheets and dialogs. These feature a 20px backdrop blur to maintain context while focusing the user.

For Admin and Seller dashboards, low-contrast outlines (1px Slate 200) are preferred over shadows to maximize information density without visual clutter.

## Shapes
The shape language is friendly and modern, utilizing **Rounded** corners across all primary UI components. 

- **Standard Buttons & Cards:** 12px (rounded-lg) to provide a soft, approachable feel.
- **Input Fields:** 8px (rounded-md) for a slightly more structured look.
- **Large Containers/Hero Sections:** 24px (rounded-xl).
- **Search Bars & Tags:** Pill-shaped (fully rounded) to differentiate search and filtering actions from primary data cards.

## Components
- **Buttons:** Primary buttons use a solid Sea Blue or Coral fill with 12px rounding. Role-specific buttons (e.g., "Confirm Delivery") use the respective role color.
- **Cards:** Product cards feature a subtle 1px border and the Level 1 shadow. On mobile, the entire card acts as a tap target with a minimum height of 120px for list views.
- **Input Fields:** Use a 16px internal padding with a 1px Slate 300 border. Focused states utilize a 2px Sea Blue border with a soft glow.
- **Chips & Tags:** Small, pill-shaped elements used for categories and status (e.g., "In Transit"). These use a 10% opacity fill of the status color with a 100% opacity text label.
- **Bottom Sheets:** The primary mobile interaction pattern for filters, seller details, and checkout summaries, featuring a top handle and 24px top-corner rounding.
- **Role Indicators:** A persistent colored bar at the very top of the screen or a tinted sidebar background to immediately signal if the user is in Admin, Seller, or Buyer mode.