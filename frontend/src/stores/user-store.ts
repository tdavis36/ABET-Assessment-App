import { defineStore } from "pinia";
import { ref, computed } from "vue";
import api from "@/api"; // axios instance with interceptor

export interface User {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: "ADMIN" | "INSTRUCTOR" | "STUDENT" | "USER";
  currentProgramId?: number;
}

export interface ProgramAccess {
  programId: number;
  isAdmin: boolean;
  role: "ADMIN" | "INSTRUCTOR";
}

export const useUserStore = defineStore("user", () => {
  // -------------------------
  // STATE
  // -------------------------
  const user = ref<User | null>(null);
  const authToken = ref<string | null>(null);
  const programs = ref<ProgramAccess[]>([]);
  const currentProgramId = ref<number | null>(null);

  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // -------------------------
  // GETTERS
  // -------------------------
  const isLoggedIn = computed(() => !!authToken.value && !!user.value);
  const isAdmin = computed(() => user.value?.role === "ADMIN");
  const isInstructor = computed(() => user.value?.role === "INSTRUCTOR");

  const userId = computed(() => user.value?.id ?? 0);

  const userFullName = computed(() => {
    if (!user.value) return "";
    if (user.value.firstName && user.value.lastName) {
      return `${user.value.firstName} ${user.value.lastName}`;
    }
    return user.value.email;
  });

  // -------------------------
  // ACTION: LOGIN
  // -------------------------
  async function login(email: string, password: string) {
    error.value = null;
    isLoading.value = true;

    try {
      const { data } = await api.post("/users/login", { email, password });

      user.value = data.user;
      authToken.value = data.authToken;
      programs.value = data.programs;
      currentProgramId.value = data.user.currentProgramId;

      // Persist to storage
      localStorage.setItem("authToken", data.authToken);
      localStorage.setItem("currentUser", JSON.stringify(data.user));
      localStorage.setItem("programs", JSON.stringify(data.programs));
      localStorage.setItem(
        "currentProgramId",
        String(data.user.currentProgramId)
      );

      return data;
    } catch (err: any) {
      error.value = err?.response?.data?.message || "Login failed";
      throw err;
    } finally {
      isLoading.value = false;
    }
  }

  // -------------------------
  // ACTION: SIGNUP
  // -------------------------
  async function signup(userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }) {
    isLoading.value = true;
    error.value = null;

    try {
      const { data } = await api.post("/users/signup", userData);

      user.value = data.user;
      authToken.value = data.authToken;
      currentProgramId.value = data.user.currentProgramId ?? null;

      localStorage.setItem("authToken", data.authToken);
      localStorage.setItem("currentUser", JSON.stringify(data.user));

      return data;
    } catch (err: any) {
      error.value = err?.response?.data?.message || "Signup failed";
      throw err;
    } finally {
      isLoading.value = false;
    }
  }

  // -------------------------
  // ACTION: SWITCH PROGRAM
  // -------------------------
  async function switchProgram(programId: number) {
    if (!authToken.value) return;

    try {
      const { data } = await api.post("/users/switch-program", { programId });

      authToken.value = data.token;
      currentProgramId.value = data.programId;
      user.value!.role = data.role;

      // Persist changes
      localStorage.setItem("authToken", data.token);
      localStorage.setItem("currentProgramId", String(data.programId));
      localStorage.setItem("currentUser", JSON.stringify(user.value));
    } catch (err) {
      console.error("Error switching program:", err);
      throw err;
    }
  }

  // -------------------------
  // LOAD FROM LOCALSTORAGE
  // -------------------------
  function loadFromStorage() {
    const token = localStorage.getItem("authToken");
    const storedUser = localStorage.getItem("currentUser");
    const storedPrograms = localStorage.getItem("programs");
    const storedPid = localStorage.getItem("currentProgramId");

    if (token) authToken.value = token;
    if (storedUser) user.value = JSON.parse(storedUser);
    if (storedPrograms) programs.value = JSON.parse(storedPrograms);
    if (storedPid) currentProgramId.value = Number(storedPid);
  }

  // -------------------------
  // ACTION: LOGOUT
  // -------------------------
  function logout() {
    user.value = null;
    authToken.value = null;
    programs.value = [];
    currentProgramId.value = null;
    error.value = null;

    localStorage.clear();
  }

  return {
    // State
    user,
    authToken,
    programs,
    currentProgramId,
    isLoading,
    error,

    // Getters
    isLoggedIn,
    isAdmin,
    isInstructor,
    userId,
    userFullName,

    // Actions
    login,
    signup,
    logout,
    switchProgram,
    loadFromStorage,
  };
});
