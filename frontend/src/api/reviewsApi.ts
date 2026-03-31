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

export type Review = {
	id: number;
	movieId: string;
	userId: number;
	reviewText: string;
	containsSpoilers: boolean;
};

async function unwrap<T>(response: Response): Promise<T> {
	const payload = (await response.json()) as ApiEnvelope<T>;
	if (!response.ok || !payload.success || payload.data == null) {
		throw new Error(payload.error?.message ?? "Review request failed");
	}
	return payload.data;
}

export async function listReviews(movieId: string): Promise<Review[]> {
	const response = await fetch(`${API_BASE_URL}/api/movies/${movieId}/reviews`, {
		method: "GET",
		headers: {
			...getAuthHeaders()
		}
	});
	return unwrap<Review[]>(response);
}

export async function createReview(movieId: string, reviewText: string, containsSpoilers: boolean): Promise<Review> {
	const response = await fetch(`${API_BASE_URL}/api/movies/${movieId}/reviews`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			...getAuthHeaders()
		},
		body: JSON.stringify({ reviewText, containsSpoilers })
	});
	return unwrap<Review>(response);
}

export async function updateReview(reviewId: number, reviewText: string, containsSpoilers: boolean): Promise<Review> {
	const response = await fetch(`${API_BASE_URL}/api/reviews/${reviewId}`, {
		method: "PATCH",
		headers: {
			"Content-Type": "application/json",
			...getAuthHeaders()
		},
		body: JSON.stringify({ reviewText, containsSpoilers })
	});
	return unwrap<Review>(response);
}

export async function deleteReview(reviewId: number): Promise<void> {
	const response = await fetch(`${API_BASE_URL}/api/reviews/${reviewId}`, {
		method: "DELETE",
		headers: {
			...getAuthHeaders()
		}
	});
	if (!response.ok) {
		throw new Error("Unable to delete review");
	}
}
