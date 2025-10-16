import { createRouter, createWebHistory } from 'vue-router'
import ConnectionTest from "@/components/ConnectionTest.vue"
import HomePage from "@/components/HomePage.vue"
import SummaryPage from "@/components/SummaryPage.vue"
import FCARPage from "@/components/FCARPage.vue"
import CourseViewPage from "@/components/CourseViewPage.vue"
import InstructorViewPage from "@/components/InstructorViewPage.vue"
import ProgramCoursesPage from "@/components/ProgramCoursesPage.vue"
import ProgramInstructorsPage from "@/components/ProgramInstructorsPage.vue"

const routes = [
  {
    path: '/test-connection',
    name: 'ConnectionTest',
    component: ConnectionTest
  },
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/:program_id/summary/',
    name: 'Summary',
    component: SummaryPage
  },
  {
    path: '/:program_id/fcar/:measure_id',
    name: 'FCAR',
    component: FCARPage
  },
  {
    path: '/:program_id/course/:course_id',
    name: 'Course',
    component: CourseViewPage
  },
  {
    path: '/:program_id/instructor/:instructor_id',
    name: 'Instructor',
    component: InstructorViewPage
  },
  {
    path: '/:program_id/courses',
    name: 'Program Courses',
    component: ProgramCoursesPage
  },
  {
    path: '/:program_id/instructors',
    name: 'Program Instructors',
    component: ProgramInstructorsPage
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes, // ← This was missing!
})

export default router
