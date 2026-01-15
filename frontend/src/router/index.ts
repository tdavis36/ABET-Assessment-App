import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user-store'
import ConnectionTest from '@/components/ConnectionTest.vue'
import HomePage from '@/pages/HomePage.vue'
import SummaryPage from '@/pages/SummaryPage.vue'
import FCARPage from '@/pages/FCARPage.vue'
import CourseViewPage from '@/pages/CourseViewPage.vue'
import InstructorViewPage from '@/pages/InstructorViewPage.vue'
import ProgramCoursesPage from '@/pages/ProgramCoursesPage.vue'
import ProgramInstructorsPage from '@/pages/ProgramInstructorsPage.vue'
import LogInPage from '@/pages/LogIn.vue'
import SignUpPage from '@/pages/SignUp.vue'
import AdminDashboard from '@/pages/AdminDashboard.vue'
import ExamplePage from "@/pages/ExamplePage.vue";
import ManagementPage from "@/pages/ManagementPage.vue";

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/test-connection',
    name: 'ConnectionTest',
    component: ConnectionTest,
  },
  {
    path: '/dashboard',
    name: 'Home',
    component: HomePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/:program_id/summary/',
    name: 'Summary',
    component: SummaryPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/fcar/:measure_id',
    name: 'FCAR',
    component: FCARPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/course/:course_id',
    name: 'Course',
    component: CourseViewPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/instructor/:instructor_id',
    name: 'Instructor',
    component: InstructorViewPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/:program_id/courses',
    name: 'Program Courses',
    component: ProgramCoursesPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/:program_id/instructors',
    name: 'Program Instructors',
    component: ProgramInstructorsPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Log In',
    component: LogInPage,
  },
  {
    path: '/signup',
    name: 'Sign Up',
    component: SignUpPage,
  },
  {
    path: '/admin-dashboard',
    name: 'Admin Dashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/setup',
    name: 'Setup',
    component: ManagementPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/examples',
    name: 'Examples',
    component: ExamplePage
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // Load user data from storage if not already loaded
  if (!userStore.isLoggedIn && localStorage.getItem('authToken')) {
    userStore.loadFromStorage()
  }

  const requiresAuth = to.meta.requiresAuth
  const requiresAdmin = to.meta.requiresAdmin

  if (requiresAuth && !userStore.isLoggedIn) {
    // Redirect to login, save intended destination
    next({
      name: 'Log In',
      query: { redirect: to.fullPath }
    })
  } else if (requiresAdmin && !userStore.isAdmin) {
    // Redirect non-admins away from admin routes
    next({ name: 'Home' })
  } else if ((to.name === 'Log In' || to.name === 'Sign Up') && userStore.isLoggedIn) {
    // Redirect authenticated users away from login/signup
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
