import { getAuthHeaders } from "../features/auth/authSession";
import { API_BASE_URL } from "./client";

export type MovieLogEntry = {
  movieId: string;
  title: string;
  releaseYear: number;
  posterUrl: string;
  rating: number | null;
  reviewId: number | null;
  reviewText: string | null;
  containsSpoilers: boolean;
  latestActivityAt: string | null;
};

export type LogResponse = {
  data: MovieLogEntry[];
  meta: {
    page: number;
    limit: number;
    total: number;
  } | null;
};

type ApiEnvelope<T> = {
  success: boolean;
  data: T | null;
  meta: LogResponse["meta"];
  error: {
    code: string;
    message: string;
    details: unknown;
  } | null;
};

export async function getMyLog(sort: "recent" | "title", page: number, limit: number): Promise<LogResponse> {
  const response = await fetch(
    `${API_BASE_URL}/api/me/log?sort=${encodeURIComponent(sort)}&page=${page}&limit=${limit}`,
    {
      method: "GET",
      headers: {
        ...getAuthHeaders()
      }
    }
  );

  const payload = (await response.json()) as ApiEnvelope<MovieLogEntry[]>;
  if (!response.ok || !payload.success || payload.data == null) {
    throw new Error(payload.error?.message ?? "Unable to load your movie log");
  }

  return {
    data: payload.data,
    meta: payload.meta
  };
}
