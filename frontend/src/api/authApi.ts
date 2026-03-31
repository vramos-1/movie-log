import { API_BASE_URL } from "./client";

type ApiEnvelope<T> = {
	success: boolean;
	data: T | null;
	meta: unknown;
	error: {
		code: string;
		message: string;
		details: unknown;
	} | null;
};

export type UserProfile = {
	id: number;
	email: string;
	username: string;
};

export type AuthResponse = {
	accessToken: string;
	tokenType: string;
	user: UserProfile;
};

export type LoginRequest = {
	email: string;
	password: string;
};

export type RegisterRequest = {
	email: string;
	username: string;
	password: string;
};

async function readEnvelope<T>(response: Response): Promise<T> {
	const payload = (await response.json()) as ApiEnvelope<T>;
	if (!response.ok || !payload.success || payload.data == null) {
		throw new Error(payload.error?.message ?? "Request failed");
	}
	return payload.data;
}

export async function login(request: LoginRequest): Promise<AuthResponse> {
	const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(request)
	});
	return readEnvelope<AuthResponse>(response);
}

export async function register(request: RegisterRequest): Promise<AuthResponse> {
	const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(request)
	});
	return readEnvelope<AuthResponse>(response);
}

export async function me(token: string): Promise<UserProfile> {
	const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
		method: "GET",
		headers: { Authorization: `Bearer ${token}` }
	});
	return readEnvelope<UserProfile>(response);
}
