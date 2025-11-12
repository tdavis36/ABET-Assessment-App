/**
 * Design Token Type Definitions
 * Use these types for type-safe access to design tokens in TypeScript
 */

export type ColorToken =
  | 'primary'
  | 'primary-light'
  | 'primary-dark'
  | 'primary-subtle'
  | 'success'
  | 'success-light'
  | 'success-dark'
  | 'error'
  | 'error-light'
  | 'error-dark'
  | 'warning'
  | 'warning-light'
  | 'warning-dark'
  | 'info'
  | 'info-light'
  | 'info-dark'
  | 'gray-50'
  | 'gray-100'
  | 'gray-200'
  | 'gray-300'
  | 'gray-400'
  | 'gray-500'
  | 'gray-600'
  | 'gray-700'
  | 'gray-800'
  | 'gray-900'

export type SpacingToken = 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl'

export type RadiusToken = 'sm' | 'md' | 'lg' | 'xl' | 'full'

export type FontSizeToken = 'xs' | 'sm' | 'base' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl'

export type FontWeightToken = 'normal' | 'medium' | 'semibold' | 'bold'

export type ShadowToken = 'sm' | 'md' | 'lg' | 'xl'

/**
 * Helper function to get CSS variable
 * @param token - The design token name
 * @returns CSS variable string
 */
export function getCssVar(token: string): string {
  return `var(--${token})`
}

/**
 * Helper function to get color CSS variable
 * @param color - The color token name
 * @returns CSS color variable string
 */
export function getColor(color: ColorToken): string {
  return `var(--color-${color})`
}

/**
 * Helper function to get spacing CSS variable
 * @param spacing - The spacing token name
 * @returns CSS spacing variable string
 */
export function getSpacing(spacing: SpacingToken): string {
  return `var(--spacing-${spacing})`
}

/**
 * Color palette object for programmatic access
 */
export const colors = {
  primary: {
    default: 'var(--color-primary)',
    light: 'var(--color-primary-light)',
    dark: 'var(--color-primary-dark)',
    subtle: 'var(--color-primary-subtle)',
  },
  success: {
    default: 'var(--color-success)',
    light: 'var(--color-success-light)',
    dark: 'var(--color-success-dark)',
  },
  error: {
    default: 'var(--color-error)',
    light: 'var(--color-error-light)',
    dark: 'var(--color-error-dark)',
  },
  warning: {
    default: 'var(--color-warning)',
    light: 'var(--color-warning-light)',
    dark: 'var(--color-warning-dark)',
  },
  info: {
    default: 'var(--color-info)',
    light: 'var(--color-info-light)',
    dark: 'var(--color-info-dark)',
  },
  gray: {
    50: 'var(--color-gray-50)',
    100: 'var(--color-gray-100)',
    200: 'var(--color-gray-200)',
    300: 'var(--color-gray-300)',
    400: 'var(--color-gray-400)',
    500: 'var(--color-gray-500)',
    600: 'var(--color-gray-600)',
    700: 'var(--color-gray-700)',
    800: 'var(--color-gray-800)',
    900: 'var(--color-gray-900)',
  },
  text: {
    primary: 'var(--color-text-primary)',
    secondary: 'var(--color-text-secondary)',
    tertiary: 'var(--color-text-tertiary)',
    muted: 'var(--color-text-muted)',
    inverse: 'var(--color-text-inverse)',
  },
  bg: {
    primary: 'var(--color-bg-primary)',
    secondary: 'var(--color-bg-secondary)',
    tertiary: 'var(--color-bg-tertiary)',
  },
  border: {
    light: 'var(--color-border-light)',
    medium: 'var(--color-border-medium)',
    dark: 'var(--color-border-dark)',
  },
}

/**
 * Spacing scale object
 */
export const spacing = {
  xs: 'var(--spacing-xs)',
  sm: 'var(--spacing-sm)',
  md: 'var(--spacing-md)',
  lg: 'var(--spacing-lg)',
  xl: 'var(--spacing-xl)',
  '2xl': 'var(--spacing-2xl)',
  '3xl': 'var(--spacing-3xl)',
}

/**
 * Border radius scale object
 */
export const radius = {
  sm: 'var(--radius-sm)',
  md: 'var(--radius-md)',
  lg: 'var(--radius-lg)',
  xl: 'var(--radius-xl)',
  full: 'var(--radius-full)',
}

/**
 * Shadow scale object
 */
export const shadow = {
  sm: 'var(--shadow-sm)',
  md: 'var(--shadow-md)',
  lg: 'var(--shadow-lg)',
  xl: 'var(--shadow-xl)',
}
