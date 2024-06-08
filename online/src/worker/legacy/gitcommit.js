
import { Octokit } from '@octokit/rest';

  // const forks = await octo.rest.repos.listForks( { owner: 'vZome', repo: 'vzome-sharing', per_page: 100 } );
  // const forkNames = forks .data .map( r => r.full_name );

export const getUserRepos = async ( { token } ) =>
{
  const octo = new Octokit( { auth: token } );
  const repos = await octo.repos.listForAuthenticatedUser( { per_page: 100 } );
  const repoNames = repos.data.map( (repo) => repo.full_name );
  return repoNames;
}

export const commitToGitHub = async ( target, uploads, commitMessage ) =>
{
  const { token, orgName, repoName, branchName } = target;

  // There are other ways to authenticate, check https://developer.github.com/v3/#authentication
  const octo = new Octokit( { auth: token } );

  // gets commit's SHA and its tree's SHA
  const currentCommit = await getCurrentCommit( octo, orgName, repoName, branchName )

  const pathsForBlobs = uploads .map( ( { path } ) => path );
  const filesBlobs = await Promise.all( uploads .map( ( { encoding, data} ) => createBlobForFile( octo, orgName, repoName )( data, encoding ) ) );
  const newTree = await createNewTree(
    octo,
    orgName, repoName,
    filesBlobs,
    pathsForBlobs,
    currentCommit.treeSha
  )
  const newCommit = await createNewCommit(
    octo,
    orgName, repoName,
    commitMessage,
    newTree.sha,
    currentCommit.commitSha
  )
  await setBranchToCommit(octo, orgName, repoName, branchName, newCommit.sha);
}


const getCurrentCommit = async (
  octo,
  org,
  repo,
  branch = 'main'
) => {
  const { data: refData } = await octo.git.getRef({
    owner: org,
    repo,
    ref: `heads/${branch}`,
  })
  const commitSha = refData.object.sha
  const { data: commitData } = await octo.git.getCommit({
    owner: org,
    repo,
    commit_sha: commitSha,
  })
  return {
    commitSha,
    treeSha: commitData.tree.sha,
  }
}

const createBlobForFile = (octo, org, repo) => async ( content, encoding ) =>
{
  const blobData = await octo.git.createBlob({
    owner: org,
    repo,
    content,
    encoding,
  })
  return blobData.data
}

const createNewTree = async (
  octo,
  owner,
  repo,
  blobs,
  paths,
  parentTreeSha
) => {
  // My custom config. Could be taken as parameters
  const tree = blobs.map(({ sha }, index) => ({
    path: paths[index],
    mode: `100644`,
    type: `blob`,
    sha,
  }));
  const { data } = await octo.git.createTree({
    owner,
    repo,
    tree,
    base_tree: parentTreeSha,
  })
  return data
}

const createNewCommit = async (
  octo,
  org,
  repo,
  message,
  currentTreeSha,
  currentCommitSha
) =>
  (await octo.git.createCommit({
    owner: org,
    repo,
    message,
    tree: currentTreeSha,
    parents: [currentCommitSha],
  })).data

const setBranchToCommit = (
  octo,
  org,
  repo,
  branch = `main`,
  commitSha
) =>
  octo.git.updateRef({
    owner: org,
    repo,
    ref: `heads/${branch}`,
    sha: commitSha,
  })