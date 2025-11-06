import { createRouter, createWebHistory } from 'vue-router'
import ConnectionTest from '@/components/ConnectionTest.vue'
import HomePage from '@/components/pages/HomePage.vue'
import SummaryPage from '@/components/pages/SummaryPage.vue'
import FCARPage from '@/components/pages/FCARPage.vue'
import CourseViewPage from '@/components/pages/CourseViewPage.vue'
import InstructorViewPage from '@/components/pages/InstructorViewPage.vue'
import ProgramCoursesPage from '@/components/pages/ProgramCoursesPage.vue'
import ProgramInstructorsPage from '@/components/pages/ProgramInstructorsPage.vue'
import LogInPage from '@/components/pages/LogIn.vue'
import SignUpPage from '@/components/pages/SignUp.vue'
import AdminDashboard from '@/components/AdminDashboard.vue'

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
  },
  {
    path: '/:program_id/summary/',
    name: 'Summary',
    component: SummaryPage,
  },
  {
    path: '/fcar/:measure_id',
    name: 'FCAR',
    component: FCARPage,
  },
  {
    path: '/course/:course_id',
    name: 'Course',
    component: CourseViewPage,
  },
  {
    path: '/instructor/:instructor_id',
    name: 'Instructor',
    component: InstructorViewPage,
  },
  {
    path: '/:program_id/courses',
    name: 'Program Courses',
    component: ProgramCoursesPage,
  },
  {
    path: '/:program_id/instructors',
    name: 'Program Instructors',
    component: ProgramInstructorsPage,
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
  },
]



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

export default router
