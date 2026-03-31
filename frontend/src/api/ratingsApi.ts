import { getAuthHeaders } from "../features/auth/authSession";
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

export type RatingResponse = {
	movieId: string;
	userId: number;
	score: number;
};

async function unwrap<T>(response: Response): Promise<T | null> {
	const payload = (await response.json()) as ApiEnvelope<T>;
	if (!response.ok || !payload.success) {
		throw new Error(payload.error?.message ?? "Rating request failed");
	}
	return payload.data;
}

export async function upsertRating(movieId: string, score: number): Promise<RatingResponse> {
	const response = await fetch(`${API_BASE_URL}/api/movies/${movieId}/rating`, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json",
			...getAuthHeaders()
		},
		body: JSON.stringify({ score })
	});

	const payload = await unwrap<RatingResponse>(response);
	if (!payload) {
		throw new Error("Rating update returned no data");
	}
	return payload;
}

export async function getMyRating(movieId: string): Promise<RatingResponse | null> {
	const response = await fetch(`${API_BASE_URL}/api/movies/${movieId}/rating/me`, {
		method: "GET",
		headers: {
			...getAuthHeaders()
		}
	});
	return unwrap<RatingResponse>(response);
}
