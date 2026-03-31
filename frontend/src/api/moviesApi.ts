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

export type MovieSearchItem = {
	id: string;
	title: string;
	releaseYear: number;
	posterUrl: string;
};

export type MovieDetails = {
	id: string;
	title: string;
	releaseYear: number;
	synopsis: string;
	posterUrl: string;
};

async function unwrap<T>(response: Response): Promise<T> {
	const payload = (await response.json()) as ApiEnvelope<T>;
	if (!response.ok || !payload.success || payload.data == null) {
		throw new Error(payload.error?.message ?? "Movie request failed");
	}
	return payload.data;
}

export async function searchMovies(query: string): Promise<MovieSearchItem[]> {
	const response = await fetch(`${API_BASE_URL}/api/movies/search?query=${encodeURIComponent(query)}`, {
		method: "GET",
		headers: {
			...getAuthHeaders()
		}
	});
	return unwrap<MovieSearchItem[]>(response);
}

export async function getMovieDetails(id: string): Promise<MovieDetails> {
	const response = await fetch(`${API_BASE_URL}/api/movies/${id}`, {
		method: "GET",
		headers: {
			...getAuthHeaders()
		}
	});
	return unwrap<MovieDetails>(response);
}
